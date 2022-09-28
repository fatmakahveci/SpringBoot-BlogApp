package com.fatmakahveci.blog.controller;

import com.fatmakahveci.blog.model.Post;
import com.fatmakahveci.blog.service.PostService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping(path = "/posts")
public class PostController {

    private PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping(path = "/{id}")
    public Post viewPostById(@PathVariable Integer id) {
        return postService.findById(id);
    }

    @GetMapping("/addNewPost")
    public ModelAndView addNewPost() {
        ModelAndView mav = new ModelAndView("post_form");
        mav.addObject("post", new Post());
        return mav;
    }
    
    @PostMapping("/savePost")
    public ModelAndView savePost(@ModelAttribute Post post) {
        postService.save(post);
        return new ModelAndView("redirect:/");
    }

    // @TODO: write editPost method

    // @TODO: write deletePost method

}