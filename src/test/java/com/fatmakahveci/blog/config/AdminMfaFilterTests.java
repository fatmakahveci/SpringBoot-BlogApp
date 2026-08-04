package com.fatmakahveci.blog.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

class AdminMfaFilterTests {

    private final AdminMfaFilter filter = new AdminMfaFilter();

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void redirectsAnUnverifiedAdministratorToTheChallenge() throws Exception {
        authenticate("ROLE_ADMIN");
        MockHttpServletRequest request = request("/posts/add");
        request.getSession().setAttribute(AdminMfaFilter.VERIFIED_SESSION_ATTRIBUTE, false);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        assertThat(response.getRedirectedUrl()).isEqualTo("/mfa");
        assertThat(chain.getRequest()).isNull();
    }

    @Test
    void returnsAStandardJsonFailureForAnUnverifiedAdministratorApiCall() throws Exception {
        authenticate("ROLE_ADMIN");
        MockHttpServletRequest request = request("/api/posts");
        request.getSession().setAttribute(AdminMfaFilter.VERIFIED_SESSION_ATTRIBUTE, false);
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertThat(response.getStatus()).isEqualTo(403);
        assertThat(response.getContentType()).isEqualTo("application/json");
        assertThat(response.getContentAsString(StandardCharsets.UTF_8)).contains("MFA_REQUIRED");
    }

    @Test
    void allowsMfaResourcesAndVerifiedAdministratorRequests() throws Exception {
        authenticate("ROLE_ADMIN");
        MockHttpServletRequest challenge = request("/mfa");
        challenge.getSession().setAttribute(AdminMfaFilter.VERIFIED_SESSION_ATTRIBUTE, false);
        MockFilterChain challengeChain = new MockFilterChain();
        filter.doFilter(challenge, new MockHttpServletResponse(), challengeChain);

        MockHttpServletRequest verified = request("/posts/add");
        verified.getSession().setAttribute(AdminMfaFilter.VERIFIED_SESSION_ATTRIBUTE, true);
        MockFilterChain verifiedChain = new MockFilterChain();
        filter.doFilter(verified, new MockHttpServletResponse(), verifiedChain);

        assertThat(challengeChain.getRequest()).isNotNull();
        assertThat(verifiedChain.getRequest()).isNotNull();
    }

    @Test
    void doesNotApplyAdministratorMfaToAuthors() throws Exception {
        authenticate("ROLE_AUTHOR");
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request("/posts/add"), new MockHttpServletResponse(), chain);

        assertThat(chain.getRequest()).isNotNull();
    }

    private MockHttpServletRequest request(String path) {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", path);
        request.setRequestURI(path);
        return request;
    }

    private void authenticate(String role) {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                "account", "unused", java.util.List.of(new SimpleGrantedAuthority(role))));
    }
}
