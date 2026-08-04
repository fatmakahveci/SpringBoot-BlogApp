package com.fatmakahveci.blog.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.fatmakahveci.blog.dao.AdminAuditEventRepository;
import com.fatmakahveci.blog.model.AdminAuditEvent;

@ExtendWith(MockitoExtension.class)
class AdminAuditFilterTests {

    @Mock
    private AdminAuditEventRepository repository;

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void recordsSuccessfulAdministratorChangesWithoutRequestContent() throws Exception {
        authenticate("ROLE_ADMIN");
        AdminAuditFilter filter = new AdminAuditFilter(
                repository, Clock.fixed(Instant.parse("2026-08-04T20:00:00Z"), ZoneOffset.UTC));
        MockHttpServletRequest request = new MockHttpServletRequest("DELETE", "/tags/7");
        request.setRequestURI("/tags/7");
        request.setRemoteAddr("192.0.2.10");
        MockHttpServletResponse response = new MockHttpServletResponse();
        response.setHeader(ObservabilityContextFilter.REQUEST_ID_HEADER, "request-7");

        filter.doFilter(request, response, new MockFilterChain());

        var eventCaptor = forClass(AdminAuditEvent.class);
        verify(repository).save(eventCaptor.capture());
        AdminAuditEvent event = eventCaptor.getValue();
        assertThat(event.getOccurredAt()).isEqualTo(Instant.parse("2026-08-04T20:00:00Z"));
        assertThat(event.getUsername()).isEqualTo("admin");
        assertThat(event.getHttpMethod()).isEqualTo("DELETE");
        assertThat(event.getRequestPath()).isEqualTo("/tags/7");
        assertThat(event.getResponseStatus()).isEqualTo(200);
        assertThat(event.getClientIp()).isEqualTo("192.0.2.10");
        assertThat(event.getRequestId()).isEqualTo("request-7");
    }

    @Test
    void ignoresReadsFailuresAndNonAdministratorChanges() throws Exception {
        AdminAuditFilter filter = new AdminAuditFilter(repository);

        authenticate("ROLE_ADMIN");
        filter.doFilter(request("GET", "/admin/audit"), new MockHttpServletResponse(), new MockFilterChain());

        MockHttpServletResponse failed = new MockHttpServletResponse();
        failed.setStatus(409);
        filter.doFilter(request("DELETE", "/tags/7"), failed, new MockFilterChain());

        authenticate("ROLE_AUTHOR");
        filter.doFilter(request("POST", "/posts"), new MockHttpServletResponse(), new MockFilterChain());

        verify(repository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    private MockHttpServletRequest request(String method, String path) {
        MockHttpServletRequest request = new MockHttpServletRequest(method, path);
        request.setRequestURI(path);
        return request;
    }

    private void authenticate(String role) {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                "admin", "unused", List.of(new SimpleGrantedAuthority(role))));
    }
}
