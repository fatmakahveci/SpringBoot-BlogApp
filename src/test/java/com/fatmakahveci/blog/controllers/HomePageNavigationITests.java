package com.fatmakahveci.blog.controllers;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class HomePageNavigationITests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void explorePostsLinksToExistingAccessibleSection() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(
                        "href=\"/#posts\" class=\"btn btn-primary btn-lg\" data-scroll-target=\"posts\"")))
                .andExpect(content().string(containsString(
                        "id=\"posts\" class=\"container-xl px-3 py-5\" tabindex=\"-1\"")))
                .andExpect(content().string(containsString("id=\"posts-heading\"")));
    }
}
