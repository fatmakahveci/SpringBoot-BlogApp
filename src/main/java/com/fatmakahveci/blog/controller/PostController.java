package com.fatmakahveci.blog.controller;

import com.fatmakahveci.blog.model.Post;
import com.fatmakahveci.blog.service.PostService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@RestController
@RequestMapping(path = "/posts")
public class PostController {
    private PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping(path = "/add")
    public Post addNewPost(@RequestBody Post post) {
        return postService.save(post);
    }

    @GetMapping(path = "/post/{id}")
    public Post getSingle(@PathVariable Integer id) {
        return postService.findById(id);
    }

    @GetMapping(path = "/all")
    public @ResponseBody Iterable<Post> findAll() {
        return postService.findAll();
    }
}