package com.fatmakahveci.blog.config;

import java.io.IOException;
import java.util.UUID;
import java.util.regex.Pattern;

import org.slf4j.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.sentry.Sentry;
import io.sentry.protocol.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@Order(Ordered.LOWEST_PRECEDENCE - 10)
public class ObservabilityContextFilter extends OncePerRequestFilter {

    static final String REQUEST_ID_HEADER = "X-Request-ID";
    private static final Pattern SAFE_REQUEST_ID = Pattern.compile("[A-Za-z0-9._-]{1,64}");
    private static final Logger LOGGER = LoggerFactory.getLogger(ObservabilityContextFilter.class);

    private final String applicationVersion;
    private final String environment;

    ObservabilityContextFilter(
            @Value("${info.app.version:dev}") String applicationVersion,
            @Value("${blog.environment:development}") String environment) {
        this.applicationVersion = applicationVersion;
        this.environment = environment;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String requestId = resolveRequestId(request.getHeader(REQUEST_ID_HEADER));
        String username = resolveUsername();
        long startedAt = System.nanoTime();
        response.setHeader(REQUEST_ID_HEADER, requestId);

        try (MDC.MDCCloseable ignoredRequest = MDC.putCloseable("request.id", requestId);
                MDC.MDCCloseable ignoredUser = MDC.putCloseable("user.name", username);
                MDC.MDCCloseable ignoredVersion = MDC.putCloseable("service.version", applicationVersion);
                MDC.MDCCloseable ignoredEnvironment = MDC.putCloseable("service.environment", environment)) {
            configureSentry(requestId, username);
            try {
                filterChain.doFilter(request, response);
            } finally {
                long durationMillis = (System.nanoTime() - startedAt) / 1_000_000;
                LOGGER.info("HTTP request completed method={} path={} status={} duration_ms={}",
                        request.getMethod(), request.getRequestURI(), response.getStatus(), durationMillis);
                clearSentryContext();
            }
        }
    }

    private String resolveRequestId(String candidate) {
        return candidate != null && SAFE_REQUEST_ID.matcher(candidate).matches()
                ? candidate
                : UUID.randomUUID().toString();
    }

    private String resolveUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() ? authentication.getName() : "anonymous";
    }

    private void configureSentry(String requestId, String username) {
        Sentry.configureScope(scope -> {
            User user = new User();
            user.setUsername(username);
            scope.setUser(user);
            scope.setTag("request_id", requestId);
            scope.setTag("service_version", applicationVersion);
            scope.setTag("environment", environment);
        });
    }

    private void clearSentryContext() {
        Sentry.configureScope(scope -> {
            scope.setUser(null);
            scope.removeTag("request_id");
            scope.removeTag("service_version");
            scope.removeTag("environment");
        });
    }
}
