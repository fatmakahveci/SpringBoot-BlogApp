package com.fatmakahveci.blog.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class SecurityConfigITests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void publicPagesRemainAccessibleWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
    }

    @Test
    void responsesIncludeConfiguredSecurityHeaders() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(header().string("Content-Security-Policy", containsString("frame-ancestors 'none'")))
                .andExpect(header().string("Referrer-Policy", "no-referrer"))
                .andExpect(header().string("Permissions-Policy", "camera=(), microphone=(), geolocation=()"))
                .andExpect(header().string("X-Content-Type-Options", "nosniff"));
    }

    @Test
    void stateChangingRequestsRequireCsrfToken() throws Exception {
        mockMvc.perform(post("/tags")
                        .with(user("author").roles("AUTHOR"))
                        .param("name", "security"))
                .andExpect(status().isForbidden());
    }

    @Test
    void anonymousUsersAreRedirectedToLoginForWriteOperations() throws Exception {
        mockMvc.perform(post("/tags")
                        .with(csrf())
                        .param("name", "security"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", "/login"));
    }

    @Test
    void authorsCannotDeleteContent() throws Exception {
        mockMvc.perform(delete("/posts/999")
                        .with(csrf())
                        .with(user("author").roles("AUTHOR")))
                .andExpect(status().isForbidden());
    }

    @Test
    void administratorsCanDeleteContent() throws Exception {
        mockMvc.perform(delete("/posts/999")
                        .with(csrf())
                        .with(user("admin").roles("ADMIN", "AUTHOR")))
                .andExpect(status().is3xxRedirection());
    }
}
