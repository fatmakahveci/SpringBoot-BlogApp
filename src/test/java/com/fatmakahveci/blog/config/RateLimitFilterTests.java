package com.fatmakahveci.blog.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import tools.jackson.databind.ObjectMapper;

class RateLimitFilterTests {

    private final Clock clock = Clock.fixed(Instant.parse("2026-08-04T20:00:00Z"), ZoneOffset.UTC);
    private final RateLimitFilter.Limits limits = new RateLimitFilter.Limits(2, 3, 4, 5, 1, 1);

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void returnsStandardJsonAndRetryAfterWhenAnonymousApiLimitIsExceeded() throws Exception {
        RateLimitFilter filter = filter("");

        MockHttpServletResponse first = execute(filter, request("GET", "/api/posts", "192.0.2.1"));
        MockHttpServletResponse second = execute(filter, request("GET", "/api/posts", "192.0.2.1"));
        MockHttpServletResponse rejected = execute(filter, request("GET", "/api/posts", "192.0.2.1"));

        assertThat(first.getStatus()).isEqualTo(200);
        assertThat(second.getHeader("X-RateLimit-Remaining")).isEqualTo("0");
        assertThat(rejected.getStatus()).isEqualTo(429);
        assertThat(rejected.getHeader("Retry-After")).isEqualTo("60");
        assertThat(rejected.getContentAsString()).contains("Too Many Requests", "/api/posts");
    }

    @Test
    void appliesStricterIndependentLoginAndRegistrationLimits() throws Exception {
        RateLimitFilter filter = filter("");

        execute(filter, request("POST", "/login", "192.0.2.2"));
        MockHttpServletResponse loginRejected = execute(filter, request("POST", "/login", "192.0.2.2"));
        execute(filter, request("POST", "/register", "192.0.2.2"));
        MockHttpServletResponse registrationRejected = execute(filter, request("POST", "/register", "192.0.2.2"));

        assertThat(loginRejected.getStatus()).isEqualTo(429);
        assertThat(loginRejected.getContentAsString()).contains("Please wait");
        assertThat(registrationRejected.getHeader("Retry-After")).isEqualTo("3600");
    }

    @Test
    void usesSeparateAuthorAdministratorAndTrustedScannerPolicies() throws Exception {
        RateLimitFilter filter = filter("198.51.100.8");

        authenticate("writer", "ROLE_AUTHOR");
        MockHttpServletResponse author = execute(filter, request("GET", "/", "192.0.2.3"));
        authenticate("admin", "ROLE_ADMIN");
        MockHttpServletResponse admin = execute(filter, request("GET", "/", "192.0.2.3"));
        SecurityContextHolder.clearContext();
        MockHttpServletResponse scanner = execute(filter, request("GET", "/", "198.51.100.8"));

        assertThat(author.getHeader("X-RateLimit-Limit")).isEqualTo("3");
        assertThat(admin.getHeader("X-RateLimit-Limit")).isEqualTo("4");
        assertThat(scanner.getHeader("X-RateLimit-Limit")).isEqualTo("5");
    }

    @Test
    void excludesStaticAssetsFromRateLimiting() throws Exception {
        RateLimitFilter filter = filter("");
        MockHttpServletResponse response = execute(filter, request("GET", "/styles/main.css", "192.0.2.4"));

        assertThat(response.getHeader("X-RateLimit-Limit")).isNull();
        assertThat(response.getStatus()).isEqualTo(200);
    }

    private RateLimitFilter filter(String scannerAddresses) {
        return new RateLimitFilter(new ObjectMapper(), clock, limits, scannerAddresses);
    }

    private MockHttpServletResponse execute(RateLimitFilter filter, MockHttpServletRequest request) throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilter(request, response, new MockFilterChain());
        return response;
    }

    private MockHttpServletRequest request(String method, String path, String address) {
        MockHttpServletRequest request = new MockHttpServletRequest(method, path);
        request.setRequestURI(path);
        request.setRemoteAddr(address);
        return request;
    }

    private void authenticate(String username, String role) {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                username, "unused", List.of(new SimpleGrantedAuthority(role))));
    }
}
