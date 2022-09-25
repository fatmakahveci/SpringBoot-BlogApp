package com.fatmakahveci.blog.controller;

import com.fatmakahveci.blog.model.Post;
import com.fatmakahveci.blog.service.PostService;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping(path = "/posts")
public class PostController {
    private PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public Post addNewPost(@RequestBody Post post) {
        return postService.save(post);
    }

    // @GetMapping
    // public @ResponseBody Iterable<Post> getAllPosts() {
    //     return postService.findAll();
    // }

    @GetMapping(path = "/{id}")
    public Post getPostById(@PathVariable Integer id) {
        return postService.findById(id);
    }

    @GetMapping
    public ModelAndView getPosts() {
        var posts = postService.findAll();
        var params = new HashMap<String, Object>();
        params.put("posts", posts);

        return new ModelAndView("post", params);
    }
}