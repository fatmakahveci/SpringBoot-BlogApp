package com.fatmakahveci.blog.config;

import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.util.Set;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fatmakahveci.blog.dao.AdminAuditEventRepository;
import com.fatmakahveci.blog.model.AdminAuditEvent;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@Order(Ordered.LOWEST_PRECEDENCE - 5)
public class AdminAuditFilter extends OncePerRequestFilter {

    private static final Set<String> READ_ONLY_METHODS = Set.of("GET", "HEAD", "OPTIONS");
    private final AdminAuditEventRepository repository;
    private final Clock clock;

    public AdminAuditFilter(AdminAuditEventRepository repository) {
        this(repository, Clock.systemUTC());
    }

    AdminAuditFilter(AdminAuditEventRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        filterChain.doFilter(request, response);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (shouldRecord(request, response, authentication)) {
            repository.save(new AdminAuditEvent(
                    Instant.now(clock),
                    authentication.getName(),
                    clipped(request.getMethod(), 10),
                    clipped(request.getRequestURI(), 300),
                    response.getStatus(),
                    clipped(request.getRemoteAddr(), 64),
                    clipped(response.getHeader(ObservabilityContextFilter.REQUEST_ID_HEADER), 64)));
        }
    }

    private boolean shouldRecord(
            HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        return !READ_ONLY_METHODS.contains(request.getMethod())
                && response.getStatus() < 400
                && authentication != null
                && authentication.isAuthenticated()
                && authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    private String clipped(String value, int maximumLength) {
        if (value == null) {
            return "unknown";
        }
        return value.length() <= maximumLength ? value : value.substring(0, maximumLength);
    }
}
