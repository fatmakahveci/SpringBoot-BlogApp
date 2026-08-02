package com.fatmakahveci.blog.controllers;

import java.util.Collections;
import java.util.Optional;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fatmakahveci.blog.model.Post;
import com.fatmakahveci.blog.model.Tag;
import com.fatmakahveci.blog.exception.DuplicatePostTitleException;
import com.fatmakahveci.blog.service.PostService;
import com.fatmakahveci.blog.service.TagService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class PostControllerITests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PostService postService;

    @MockitoBean
    private TagService tagService;

    @Test
    void canRenderNewPostForm() throws Exception {
        mockMvc.perform(get("/posts/add")
                .with(user("author").roles("AUTHOR")))
                .andExpect(status().isOk())
                .andExpect(view().name("post_form"))
                .andExpect(model().attributeExists("post"))
                .andExpect(model().attribute("post", new Post()))
                .andExpect(model().attributeExists("tag"))
                .andExpect(model().attribute("tag", new Tag()))
                .andExpect(model().attributeExists("tags"))
                .andExpect(model().attribute("tags", Matchers.empty()));
    }

    @Test
    void canSavePost() throws Exception {
        mockMvc.perform(post("/posts")
                .with(csrf())
                .with(user("author").roles("AUTHOR"))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("title", "title")
                .param("content", "content"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void rejectsSavePostWithoutCsrfToken() throws Exception {
        mockMvc.perform(post("/posts")
                .with(user("author").roles("AUTHOR"))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("title", "title")
                .param("content", "content"))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateUsesPathIdInsteadOfSubmittedId() throws Exception {
        Post existingPost = new Post(1, "old title", "old content", Collections.emptySet());
        when(postService.findById(1)).thenReturn(Optional.of(existingPost));

        mockMvc.perform(post("/posts/1")
                .with(csrf())
                .with(user("author").roles("AUTHOR"))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("id", "999")
                .param("title", "new title")
                .param("content", "new content"))
                .andExpect(status().is3xxRedirection());

        ArgumentCaptor<Post> captor = ArgumentCaptor.forClass(Post.class);
        verify(postService).save(captor.capture());
        assertEquals(1, captor.getValue().getId());
    }

    @Test
    void getPostFromForm() throws Exception {
        Post post = new Post(1, "title", "content", Collections.emptySet());
        
        when(postService.findById(1)).thenReturn(Optional.of(post));

        mockMvc.perform(get("/posts/{id}", "1")
               .with(user("author").roles("AUTHOR")))
               .andExpect(status().isOk())
               .andExpect(content().contentType("text/html;charset=UTF-8"));
    }

    @Test
    void missingPostReturnsNotFound() throws Exception {
        when(postService.findById(404)).thenReturn(Optional.empty());

        mockMvc.perform(get("/posts/404")
                .with(user("author").roles("AUTHOR")))
                .andExpect(status().isNotFound());
    }

    @Test
    void blankTitleIsRejected() throws Exception {
        mockMvc.perform(post("/posts")
                .with(csrf())
                .with(user("author").roles("AUTHOR"))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("title", " ")
                .param("content", "content"))
                .andExpect(status().isOk())
                .andExpect(view().name("post_form"))
                .andExpect(model().attributeHasFieldErrors("post", "title"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Title is required.")));

        verify(postService, never()).save(any(Post.class));
    }

    @Test
    void invalidUpdateReturnsToFormWithPathId() throws Exception {
        Post existingPost = new Post(7, "Existing", "Content", Collections.emptySet());
        when(postService.findById(7)).thenReturn(Optional.of(existingPost));

        mockMvc.perform(post("/posts/7")
                .with(csrf())
                .with(user("author").roles("AUTHOR"))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("id", "999")
                .param("title", " "))
                .andExpect(status().isOk())
                .andExpect(view().name("update_post_form"))
                .andExpect(model().attributeHasFieldErrors("post", "title"))
                .andExpect(model().attribute("post", org.hamcrest.Matchers.hasProperty("id", org.hamcrest.Matchers.is(7))));

        verify(postService, never()).save(any(Post.class));
    }

    @Test
    void duplicatePostTitleReturnsToFormWithMessage() throws Exception {
        when(postService.save(any(Post.class))).thenThrow(new DuplicatePostTitleException("Existing"));

        mockMvc.perform(post("/posts")
                .with(csrf())
                .with(user("author").roles("AUTHOR"))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("title", "Existing")
                .param("content", "content"))
                .andExpect(status().isOk())
                .andExpect(view().name("post_form"))
                .andExpect(model().attributeHasFieldErrors("post", "title"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "A post titled &#39;Existing&#39; already exists.")));
    }
}
