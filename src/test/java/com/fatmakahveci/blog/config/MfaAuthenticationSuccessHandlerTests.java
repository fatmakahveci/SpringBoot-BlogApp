package com.fatmakahveci.blog.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

class MfaAuthenticationSuccessHandlerTests {

    private final MfaAuthenticationSuccessHandler handler = new MfaAuthenticationSuccessHandler();

    @Test
    void sendsAdministratorsToMfaWithAnUnverifiedSession() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        handler.onAuthenticationSuccess(request, response, authentication("ROLE_ADMIN"));

        assertThat(response.getRedirectedUrl()).isEqualTo("/mfa");
        assertThat(request.getSession().getAttribute(AdminMfaFilter.VERIFIED_SESSION_ATTRIBUTE)).isEqualTo(false);
    }

    @Test
    void sendsAuthorsHomeWithoutAnMfaChallenge() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        handler.onAuthenticationSuccess(request, response, authentication("ROLE_AUTHOR"));

        assertThat(response.getRedirectedUrl()).isEqualTo("/");
        assertThat(request.getSession().getAttribute(AdminMfaFilter.VERIFIED_SESSION_ATTRIBUTE)).isEqualTo(true);
    }

    private UsernamePasswordAuthenticationToken authentication(String role) {
        return new UsernamePasswordAuthenticationToken(
                "account", "unused", List.of(new SimpleGrantedAuthority(role)));
    }
}
