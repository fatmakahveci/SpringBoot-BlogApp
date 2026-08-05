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
    void browsePostsLinksToExistingAccessibleSection() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(
                        "href=\"#posts\" class=\"btn btn-primary btn-lg\" data-scroll-target=\"posts\"")))
                .andExpect(content().string(containsString(
                        "id=\"posts\" class=\"container-xl px-3 py-5\" tabindex=\"-1\"")))
                .andExpect(content().string(containsString("id=\"posts-heading\"")));
    }

    @Test
    void pageAdvertisesTheCompleteBrandedIconSet() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(
                        "rel=\"icon\" type=\"image/svg+xml\" href=\"/images/favicon.svg?v=1\"")))
                .andExpect(content().string(containsString(
                        "rel=\"icon\" type=\"image/png\" sizes=\"32x32\" href=\"/images/favicon-32x32.png?v=1\"")))
                .andExpect(content().string(containsString(
                        "rel=\"apple-touch-icon\" sizes=\"180x180\" href=\"/images/apple-touch-icon.png?v=1\"")))
                .andExpect(content().string(containsString(
                        "class=\"brand-mark\" src=\"/images/favicon.svg?v=1\" width=\"36\" height=\"36\" alt=\"\"")));

        mockMvc.perform(get("/images/favicon.svg"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("image/svg+xml"))
                .andExpect(content().string(containsString("Spring Blog logo")));

        mockMvc.perform(get("/images/favicon-32x32.png"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("image/png"));

        mockMvc.perform(get("/images/apple-touch-icon.png"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("image/png"));
    }
}
