package com.fatmakahveci.blog.controllers;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.cache.CacheManager;
import org.springframework.test.web.servlet.MockMvc;

import com.fatmakahveci.blog.model.Post;
import com.fatmakahveci.blog.model.PostStatus;
import com.fatmakahveci.blog.model.Tag;
import com.fatmakahveci.blog.service.PostService;
import com.fatmakahveci.blog.service.TagService;

import static com.fatmakahveci.blog.support.PostFixtures.aDraftPost;
import static com.fatmakahveci.blog.support.PostFixtures.aPublishedPost;

@SpringBootTest(properties = "blog.seo.base-url=https://blog.example.com")
@AutoConfigureMockMvc
class SeoControllerITests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CacheManager cacheManager;

    @MockitoBean
    private PostService postService;

    @MockitoBean
    private TagService tagService;

    @BeforeEach
    void clearSitemapCache() {
        cacheManager.getCache("seo-sitemap").clear();
    }

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
        Post published = aPublishedPost().id(null).title("Published").slug("published-post").build();
        Post draft = aDraftPost().id(null).title("Draft").slug("draft-post").build();
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

    @Test
    void sitemapUsesTheConfiguredCache() throws Exception {
        given(postService.findPublished()).willReturn(List.of());
        given(tagService.findByPostStatus(PostStatus.PUBLISHED)).willReturn(List.of());

        mockMvc.perform(get("/sitemap.xml")).andExpect(status().isOk());
        mockMvc.perform(get("/sitemap.xml")).andExpect(status().isOk());

        verify(postService, times(1)).findPublished();
        verify(tagService, times(1)).findByPostStatus(PostStatus.PUBLISHED);
    }
}
