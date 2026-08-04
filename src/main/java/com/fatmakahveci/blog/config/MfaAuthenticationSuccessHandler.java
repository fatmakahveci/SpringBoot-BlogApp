package com.fatmakahveci.blog.config;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class MfaAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        boolean administrator = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        request.getSession().setAttribute(AdminMfaFilter.VERIFIED_SESSION_ATTRIBUTE, !administrator);
        response.sendRedirect(request.getContextPath() + (administrator ? "/mfa" : "/"));
    }
}
