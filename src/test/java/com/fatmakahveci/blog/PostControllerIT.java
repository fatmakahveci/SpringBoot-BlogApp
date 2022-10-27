package com.fatmakahveci.blog;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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

import com.fatmakahveci.blog.dao.PostRepository;
import com.fatmakahveci.blog.model.Post;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class PostControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    void setup(){
        postRepository.deleteAll();
    }

    @Test
    public void givenListOfPosts_whenGetAllPosts_thenReturnPostsList() throws Exception{
        List<Post> posts = new ArrayList<>();
        Post post1 = new Post(1, "first title", "first content", Collections.emptySet());
        Post post2 = new Post(2, "second title", "second content", Collections.emptySet());
        posts.add(post1);
        posts.add(post2);
        postRepository.saveAll(posts);
    
        ResultActions response = mockMvc.perform(get("/posts"));

        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.size()", is(posts.size())));

    }
}