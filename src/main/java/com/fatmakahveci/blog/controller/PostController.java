package com.fatmakahveci.blog.controller;

import com.fatmakahveci.blog.model.Post;
import com.fatmakahveci.blog.service.PostService;

import java.util.HashMap;
import java.util.List;

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
    public void savePost(@RequestBody Post post) {
        postService.save(post);
    }

    @GetMapping(path = "/{id}")
    public Post viewPostById(@PathVariable Integer id) {
        return postService.findById(id);
    }

    @GetMapping(path = {"","/"})
    public List<Post> viewAllPosts() {
        return postService.findAll();
    }

    @GetMapping(path = "/createNewPost")
    public ModelAndView createNewPost() {
        var posts = postService.findAll();
        var params = new HashMap<String, List<Post>>();
        params.put("posts", posts);
        return new ModelAndView("post_form", params);
    }

    // @TODO: write editPost method

    // @TODO: write deletePost method

}