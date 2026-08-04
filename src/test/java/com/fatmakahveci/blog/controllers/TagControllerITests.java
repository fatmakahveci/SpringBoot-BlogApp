package com.fatmakahveci.blog.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fatmakahveci.blog.model.Tag;
import com.fatmakahveci.blog.model.Post;
import com.fatmakahveci.blog.exception.DuplicateTagNameException;
import com.fatmakahveci.blog.service.PostService;
import com.fatmakahveci.blog.service.TagService;

import static com.fatmakahveci.blog.support.PostFixtures.aPublishedPost;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class TagControllerITests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TagService tagService;

    @MockitoBean
    private PostService postService;

    @Test
    void missingTagReturnsNotFound() throws Exception {
        when(tagService.findById(404)).thenReturn(Optional.empty());

        mockMvc.perform(get("/tags/404"))
                .andExpect(status().isNotFound());
    }

    @Test
    void emptyTagPageShowsHelpfulEmptyState() throws Exception {
        when(tagService.findById(1)).thenReturn(Optional.of(new Tag(1, "java", Set.of())));

        mockMvc.perform(get("/tags/1"))
                .andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content()
                        .string(org.hamcrest.Matchers.containsString("No published posts use this tag yet.")));
    }

    @Test
    void administratorsSeeDeleteConfirmationHook() throws Exception {
        Tag tag = new Tag(1, "java", Set.of());
        Post post = aPublishedPost().title("Post").slug("post").tags(tag).build();
        when(tagService.findById(1)).thenReturn(Optional.of(tag));

        mockMvc.perform(get("/tags/1").with(user("admin").roles("ADMIN", "AUTHOR")))
                .andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content()
                        .string(org.hamcrest.Matchers.containsString(
                                "data-confirm=\"Delete this post permanently?\"")))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content()
                        .string(org.hamcrest.Matchers.containsString("/scripts/main.js")));
    }

    @Test
    void blankTagNameIsRejected() throws Exception {
        mockMvc.perform(post("/tags")
                .with(csrf())
                .with(user("author").roles("AUTHOR"))
                .param("name", " "))
                .andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.view().name("index"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.model()
                        .attributeHasFieldErrors("tag", "name"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content()
                        .string(org.hamcrest.Matchers.containsString("Tag name is required.")));

        verify(tagService, never()).createByName(any());
    }

    @Test
    void duplicateTagNameReturnsToHomeWithMessage() throws Exception {
        when(tagService.createByName("java")).thenThrow(new DuplicateTagNameException("java"));

        mockMvc.perform(post("/tags")
                .with(csrf())
                .with(user("author").roles("AUTHOR"))
                .param("name", "java"))
                .andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.view().name("index"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.model()
                        .attributeHasFieldErrors("tag", "name"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content()
                        .string(org.hamcrest.Matchers.containsString("A tag named &#39;java&#39; already exists.")));
    }
    @Test
    void administratorCanOpenTopicEditor() throws Exception {
        when(tagService.findById(1)).thenReturn(Optional.of(new Tag(1, "java", Set.of())));

        mockMvc.perform(get("/tags/1/edit").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.view().name("tag_form"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content()
                        .string(org.hamcrest.Matchers.containsString("Edit topic")));
    }

    @Test
    void nonAdministratorCannotOpenTopicEditor() throws Exception {
        mockMvc.perform(get("/tags/1/edit").with(user("author").roles("AUTHOR")))
                .andExpect(status().isForbidden());
    }

    @Test
    void administratorCanUpdateTopic() throws Exception {
        mockMvc.perform(put("/tags/1")
                .with(csrf())
                .with(user("admin").roles("ADMIN"))
                .param("name", "Spring Boot"))
                .andExpect(status().is3xxRedirection())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl("/tags/1"));

        verify(tagService).updateName(1, "Spring Boot");
    }

    @Test
    void topicUpdateRejectsBlankName() throws Exception {
        mockMvc.perform(put("/tags/1")
                .with(csrf())
                .with(user("admin").roles("ADMIN"))
                .param("name", " "))
                .andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.view().name("tag_form"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.model()
                        .attributeHasFieldErrors("tag", "name"));

        verify(tagService, never()).updateName(any(), any());
    }

    @Test
    void topicUpdateRequiresCsrfToken() throws Exception {
        mockMvc.perform(put("/tags/1")
                .with(user("admin").roles("ADMIN"))
                .param("name", "Spring Boot"))
                .andExpect(status().isForbidden());

        verify(tagService, never()).updateName(any(), any());
    }
}
