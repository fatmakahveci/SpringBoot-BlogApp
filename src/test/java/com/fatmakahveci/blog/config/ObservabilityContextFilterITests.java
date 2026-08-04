package com.fatmakahveci.blog.config;

import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class ObservabilityContextFilterITests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void preservesSafeClientRequestId() throws Exception {
        mockMvc.perform(get("/actuator/health").header("X-Request-ID", "client-request_42").with(user("author")))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Request-ID", "client-request_42"));
    }

    @Test
    void replacesUnsafeRequestId() throws Exception {
        mockMvc.perform(get("/actuator/health").header("X-Request-ID", "unsafe header value"))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Request-ID",
                        matchesPattern("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}")));
    }
}
