package com.fatmakahveci.blog.controllers;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fatmakahveci.blog.model.Post;
import com.fatmakahveci.blog.model.PostStatus;
import com.fatmakahveci.blog.service.PostService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class PostRestControllerITests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PostService postService;

    @Test
    void returnsPaginatedPosts() throws Exception {
        Post post = new Post(1, "title", "content", Collections.emptySet());
        given(postService.findAll(eq(""), any(Pageable.class), eq(false)))
                .willReturn(new PageImpl<>(List.of(post)));

        mockMvc.perform(get("/api/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("title"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void returnsEmptyPageWhenThereAreNoPosts() throws Exception {
        given(postService.findAll(eq(""), any(Pageable.class), eq(false)))
                .willReturn(new PageImpl<>(Collections.emptyList()));

        mockMvc.perform(get("/api/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    void returnsPublishedPostBySlug() throws Exception {
        Post post = post("published-post", PostStatus.PUBLISHED);
        given(postService.findBySlug("published-post")).willReturn(Optional.of(post));

        mockMvc.perform(get("/api/posts/published-post"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("published-post"));
    }

    @Test
    void hidesDraftFromAnonymousUsers() throws Exception {
        Post post = post("draft-post", PostStatus.DRAFT);
        given(postService.findBySlug("draft-post")).willReturn(Optional.of(post));

        mockMvc.perform(get("/api/posts/draft-post"))
                .andExpect(status().isNotFound());
    }

    @Test
    void returnsDraftToAuthors() throws Exception {
        Post post = post("draft-post", PostStatus.DRAFT);
        given(postService.findBySlug("draft-post")).willReturn(Optional.of(post));

        mockMvc.perform(get("/api/posts/draft-post").with(user("author").roles("AUTHOR")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DRAFT"));
    }

    private Post post(String slug, PostStatus status) {
        Post post = new Post(1, "title", "content", Collections.emptySet());
        post.setSlug(slug);
        post.setStatus(status);
        return post;
    }
}
