package com.fatmakahveci.blog.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import static org.mockito.BDDMockito.given;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fatmakahveci.blog.model.Tag;
import com.fatmakahveci.blog.service.TagService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class TagControllerITests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TagService tagService;

    @Test
    public void givenListOfTags_whenGetAllTags_thenReturnTagsList() throws Exception {
        List<Tag> tags = new ArrayList<>();
        tags.add(new Tag(1, "first tag", Collections.emptySet()));
        given(tagService.findAll()).willReturn(tags);
    
        ResultActions response = mockMvc.perform(get("/tags"));

        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.size()", is(tags.size())));
    }
}