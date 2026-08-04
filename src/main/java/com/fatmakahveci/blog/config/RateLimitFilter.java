package com.fatmakahveci.blog.config;

import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fatmakahveci.blog.exception.ApiError;

import tools.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@Order(Ordered.LOWEST_PRECEDENCE - 20)
public class RateLimitFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;
    private final Clock clock;
    private final Map<String, Window> windows = new ConcurrentHashMap<>();
    private final Set<String> trustedScannerAddresses;
    private final Limits limits;

    @Autowired
    public RateLimitFilter(
            ObjectMapper objectMapper,
            @Value("${blog.rate-limit.anonymous-per-minute:120}") int anonymousLimit,
            @Value("${blog.rate-limit.author-per-minute:300}") int authorLimit,
            @Value("${blog.rate-limit.admin-per-minute:600}") int adminLimit,
            @Value("${blog.rate-limit.scanner-per-minute:1200}") int scannerLimit,
            @Value("${blog.rate-limit.login-per-minute:5}") int loginLimit,
            @Value("${blog.rate-limit.register-per-hour:3}") int registerLimit,
            @Value("${blog.rate-limit.trusted-scanner-addresses:}") String scannerAddresses) {
        this(objectMapper, Clock.systemUTC(),
                new Limits(anonymousLimit, authorLimit, adminLimit, scannerLimit, loginLimit, registerLimit),
                scannerAddresses);
    }

    RateLimitFilter(ObjectMapper objectMapper, Clock clock, Limits limits, String scannerAddresses) {
        this.objectMapper = objectMapper;
        this.clock = clock;
        this.limits = limits;
        this.trustedScannerAddresses = Arrays.stream(scannerAddresses.split(","))
                .map(String::trim)
                .filter(address -> !address.isEmpty())
                .collect(java.util.stream.Collectors.toUnmodifiableSet());
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        Policy policy = resolvePolicy(request);
        long now = clock.instant().getEpochSecond();
        String key = policy.name() + ':' + identity(request);
        Window window = windows.compute(key, (ignored, current) ->
                current == null || now >= current.resetAt()
                        ? new Window(1, now + policy.windowSeconds())
                        : new Window(current.count() + 1, current.resetAt()));
        int remaining = Math.max(0, policy.limit() - window.count());
        response.setHeader("X-RateLimit-Limit", Integer.toString(policy.limit()));
        response.setHeader("X-RateLimit-Remaining", Integer.toString(remaining));

        if (window.count() > policy.limit()) {
            long retryAfter = Math.max(1, window.resetAt() - now);
            response.setHeader("Retry-After", Long.toString(retryAfter));
            writeRateLimitResponse(request, response);
            return;
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/styles/")
                || path.startsWith("/scripts/")
                || path.startsWith("/images/")
                || path.startsWith("/webjars/");
    }

    private Policy resolvePolicy(HttpServletRequest request) {
        if ("POST".equals(request.getMethod()) && "/login".equals(request.getRequestURI())) {
            return new Policy("login", limits.login(), 60);
        }
        if ("POST".equals(request.getMethod()) && "/register".equals(request.getRequestURI())) {
            return new Policy("register", limits.register(), 3600);
        }
        if (trustedScannerAddresses.contains(request.getRemoteAddr())) {
            return new Policy("scanner", limits.scanner(), 60);
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (isSignedIn(authentication)) {
            boolean admin = authentication.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
            return admin
                    ? new Policy("admin", limits.admin(), 60)
                    : new Policy("author", limits.author(), 60);
        }
        return new Policy("anonymous", limits.anonymous(), 60);
    }

    private String identity(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return isSignedIn(authentication)
                ? authentication.getName()
                : request.getRemoteAddr();
    }

    private boolean isSignedIn(Authentication authentication) {
        return authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
    }

    private void writeRateLimitResponse(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setStatus(429);
        if (request.getRequestURI().startsWith("/api/")) {
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            objectMapper.writeValue(response.getWriter(), new ApiError(
                    Instant.now(clock), 429, "Too Many Requests",
                    "The request rate limit has been exceeded.", request.getRequestURI()));
            return;
        }
        response.setContentType(MediaType.TEXT_HTML_VALUE);
        response.getWriter().write("""
                <!doctype html><html lang="en"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title>Too many requests</title></head><body><main><h1>Too many requests</h1><p>Please wait before trying again.</p></main></body></html>
                """);
    }

    record Limits(int anonymous, int author, int admin, int scanner, int login, int register) { }
    private record Policy(String name, int limit, long windowSeconds) { }
    private record Window(int count, long resetAt) { }
}
