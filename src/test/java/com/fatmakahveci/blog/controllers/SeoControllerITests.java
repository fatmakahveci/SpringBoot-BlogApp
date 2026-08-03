package com.fatmakahveci.blog.controllers;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fatmakahveci.blog.model.Post;
import com.fatmakahveci.blog.model.PostStatus;
import com.fatmakahveci.blog.model.Tag;
import com.fatmakahveci.blog.service.PostService;
import com.fatmakahveci.blog.service.TagService;

@SpringBootTest(properties = "blog.seo.base-url=https://blog.example.com")
@AutoConfigureMockMvc
class SeoControllerITests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PostService postService;

    @MockitoBean
    private TagService tagService;

    @Test
    void robotsAllowsPublicPagesAndAdvertisesSitemap() throws Exception {
        mockMvc.perform(get("/robots.txt"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/plain"))
                .andExpect(content().string(containsString("User-agent: *")))
                .andExpect(content().string(containsString("Disallow: /api/")))
                .andExpect(content().string(containsString(
                        "Sitemap: https://blog.example.com/sitemap.xml")));
    }

    @Test
    void sitemapContainsOnlyCanonicalPublicContent() throws Exception {
        Post published = post("Published", "published-post", PostStatus.PUBLISHED);
        Post draft = post("Draft", "draft-post", PostStatus.DRAFT);
        Tag publicTag = new Tag(7, "java", Set.of(published));
        Tag draftTag = new Tag(8, "private", Set.of(draft));
        given(postService.findPublished()).willReturn(List.of(published));
        given(tagService.findByPostStatus(PostStatus.PUBLISHED)).willReturn(List.of(publicTag));

        mockMvc.perform(get("/sitemap.xml"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/xml"))
                .andExpect(content().string(containsString("<loc>https://blog.example.com/</loc>")))
                .andExpect(content().string(containsString(
                        "<loc>https://blog.example.com/p/published-post</loc>")))
                .andExpect(content().string(containsString(
                        "<loc>https://blog.example.com/tags/7</loc>")))
                .andExpect(content().string(org.hamcrest.Matchers.not(containsString("draft-post"))))
                .andExpect(content().string(org.hamcrest.Matchers.not(containsString("/tags/8"))));
    }

    private Post post(String title, String slug, PostStatus status) {
        Post post = new Post(null, title, "Content", Set.of());
        post.setSlug(slug);
        post.setStatus(status);
        post.setCreatedAt(Instant.parse("2026-08-03T08:00:00Z"));
        post.setUpdatedAt(Instant.parse("2026-08-03T09:00:00Z"));
        return post;
    }
}
