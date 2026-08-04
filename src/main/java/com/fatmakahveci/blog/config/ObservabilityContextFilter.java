package com.fatmakahveci.blog.config;

import java.io.IOException;
import java.util.UUID;
import java.util.regex.Pattern;

import org.slf4j.MDC;
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

    private final String applicationVersion;

    ObservabilityContextFilter(@Value("${info.app.version:dev}") String applicationVersion) {
        this.applicationVersion = applicationVersion;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String requestId = resolveRequestId(request.getHeader(REQUEST_ID_HEADER));
        String username = resolveUsername();
        response.setHeader(REQUEST_ID_HEADER, requestId);

        try (MDC.MDCCloseable ignoredRequest = MDC.putCloseable("request.id", requestId);
                MDC.MDCCloseable ignoredUser = MDC.putCloseable("user.name", username);
                MDC.MDCCloseable ignoredVersion = MDC.putCloseable("service.version", applicationVersion)) {
            configureSentry(requestId, username);
            filterChain.doFilter(request, response);
        } finally {
            clearSentryContext();
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
        });
    }

    private void clearSentryContext() {
        Sentry.configureScope(scope -> {
            scope.setUser(null);
            scope.removeTag("request_id");
            scope.removeTag("service_version");
        });
    }
}
