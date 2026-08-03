package com.fatmakahveci.blog.controllers;

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
import com.fatmakahveci.blog.service.PostService;

import static com.fatmakahveci.blog.support.PostFixtures.DEFAULT_SLUG;
import static com.fatmakahveci.blog.support.PostFixtures.DEFAULT_TITLE;
import static com.fatmakahveci.blog.support.PostFixtures.aDraftPost;
import static com.fatmakahveci.blog.support.PostFixtures.aPost;
import static com.fatmakahveci.blog.support.PostFixtures.aPublishedPost;

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
        Post post = aPost().build();
        given(postService.findAll(eq(""), any(Pageable.class), eq(false)))
                .willReturn(new PageImpl<>(List.of(post)));

        mockMvc.perform(get("/api/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value(DEFAULT_TITLE))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void returnsEmptyPageWhenThereAreNoPosts() throws Exception {
        given(postService.findAll(eq(""), any(Pageable.class), eq(false)))
                .willReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    void returnsPublishedPostBySlug() throws Exception {
        Post post = aPublishedPost().build();
        given(postService.findBySlug(DEFAULT_SLUG)).willReturn(Optional.of(post));

        mockMvc.perform(get("/api/posts/" + DEFAULT_SLUG))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value(DEFAULT_SLUG));
    }

    @Test
    void hidesDraftFromAnonymousUsers() throws Exception {
        Post post = aDraftPost().build();
        given(postService.findBySlug(DEFAULT_SLUG)).willReturn(Optional.of(post));

        mockMvc.perform(get("/api/posts/" + DEFAULT_SLUG))
                .andExpect(status().isNotFound());
    }

    @Test
    void returnsDraftToAuthors() throws Exception {
        Post post = aDraftPost().build();
        given(postService.findBySlug(DEFAULT_SLUG)).willReturn(Optional.of(post));

        mockMvc.perform(get("/api/posts/" + DEFAULT_SLUG).with(user("author").roles("AUTHOR")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DRAFT"));
    }
}
