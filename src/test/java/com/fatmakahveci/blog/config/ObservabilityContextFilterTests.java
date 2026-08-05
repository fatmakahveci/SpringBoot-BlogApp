package com.fatmakahveci.blog.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

class ObservabilityContextFilterTests {

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
        MDC.clear();
    }

    @Test
    void exposesCompleteCorrelationContextOnlyForTheCurrentRequest() throws Exception {
        ObservabilityContextFilter filter = new ObservabilityContextFilter("1.2.3", "staging");
        SecurityContextHolder.getContext().setAuthentication(
                UsernamePasswordAuthenticationToken.authenticated("author", "unused", java.util.List.of()));
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/posts");
        request.setRequestURI("/api/posts");
        request.addHeader("X-Request-ID", "request-123");
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicReference<Map<String, String>> contextDuringRequest = new AtomicReference<>();

        filter.doFilter(request, response, (ignoredRequest, ignoredResponse) ->
                contextDuringRequest.set(MDC.getCopyOfContextMap()));

        assertThat(contextDuringRequest.get()).containsEntry("request.id", "request-123")
                .containsEntry("user.name", "author")
                .containsEntry("service.version", "1.2.3")
                .containsEntry("service.environment", "staging");
        assertThat(MDC.getCopyOfContextMap()).isNullOrEmpty();
    }

    @Test
    void rejectsInvalidSentryTraceSamplingConfiguration() {
        org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new SentryConfiguration("", "test", "1.2.3", 1.01));
    }
}
