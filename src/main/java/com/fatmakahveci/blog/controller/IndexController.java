package com.fatmakahveci.blog.controller;

import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import com.fatmakahveci.blog.model.Post;
import com.fatmakahveci.blog.service.PostService;

@Controller
public class IndexController {
    private PostService postService;

    public IndexController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping({"/",""})
    public ModelAndView getPosts() {
        var posts = postService.findAll();
        var params = new HashMap<String, List<Post>>();
        params.put("posts", posts);
        return new ModelAndView("index", params);
    }
}
