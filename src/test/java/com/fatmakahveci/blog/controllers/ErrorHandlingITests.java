package com.fatmakahveci.blog.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fatmakahveci.blog.exception.DuplicateTagNameException;
import com.fatmakahveci.blog.service.PostService;
import com.fatmakahveci.blog.service.TagService;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Import(ErrorHandlingITests.ConflictController.class)
class ErrorHandlingITests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PostService postService;

    @MockitoBean
    private TagService tagService;

    @Test
    void apiNotFoundUsesStandardJsonError() throws Exception {
        when(postService.findBySlug("missing")).thenReturn(java.util.Optional.empty());

        mockMvc.perform(get("/api/posts/missing"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Could not find post missing"))
                .andExpect(jsonPath("$.path").value("/api/posts/missing"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void htmlNotFoundRendersStandardErrorPage() throws Exception {
        when(tagService.findById(404)).thenReturn(java.util.Optional.empty());

        mockMvc.perform(get("/tags/404"))
                .andExpect(status().isNotFound())
                .andExpect(view().name("error/error"))
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(content().string(containsString("Could not find tag 404")))
                .andExpect(content().string(containsString("Back to the blog")));
    }

    @Test
    void invalidApiParameterUsesStandardBadRequestResponse() throws Exception {
        mockMvc.perform(get("/api/posts").param("page", "not-a-number"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("The request is invalid."));
    }

    @Test
    void apiDuplicateUsesStandardConflictResponse() throws Exception {
        mockMvc.perform(get("/api/test/conflict"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").value("A tag named 'duplicate' already exists."));
    }

    @RestController
    static class ConflictController {

        @GetMapping("/api/test/conflict")
        void conflict() {
            throw new DuplicateTagNameException("duplicate");
        }
    }
}
