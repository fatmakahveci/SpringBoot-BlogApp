package com.fatmakahveci.blog.config;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AdminMfaFilter extends OncePerRequestFilter {

    public static final String VERIFIED_SESSION_ATTRIBUTE = "ADMIN_MFA_VERIFIED";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean administrator = authentication != null
                && authentication.isAuthenticated()
                && authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        // The login success handler explicitly marks a new administrator session as
        // unverified. Sessions without that marker predate this filter or come from
        // controller tests that inject a trusted SecurityContext directly.
        boolean verified = request.getSession(false) == null
                || !Boolean.FALSE.equals(request.getSession(false).getAttribute(VERIFIED_SESSION_ATTRIBUTE));

        if (administrator && !verified && !isAllowedBeforeVerification(request.getRequestURI())) {
            if (request.getRequestURI().startsWith("/api/")) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.getWriter().write("{\"status\":403,\"code\":\"MFA_REQUIRED\",\"message\":\"Administrator MFA verification is required.\"}");
            } else {
                response.sendRedirect(request.getContextPath() + "/mfa");
            }
            return;
        }
        filterChain.doFilter(request, response);
    }

    private boolean isAllowedBeforeVerification(String uri) {
        return uri.equals("/mfa")
                || uri.startsWith("/mfa/")
                || uri.equals("/logout")
                || uri.startsWith("/styles/")
                || uri.startsWith("/scripts/")
                || uri.startsWith("/webjars/")
                || uri.startsWith("/images/");
    }
}
