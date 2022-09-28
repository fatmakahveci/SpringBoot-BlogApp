package com.fatmakahveci.blog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import com.fatmakahveci.blog.model.Post;
import com.fatmakahveci.blog.service.PostService;

@Controller
public class MainController {
    private PostService postService;

    @Autowired
    public MainController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping({"/",""})
    public ModelAndView viewHomePage() {
        ModelAndView mav = new ModelAndView("index");
		mav.addObject("posts", postService.findAll());
        return mav;
    }

    @GetMapping("/addNewPost")
    public ModelAndView addNewPost() {
        ModelAndView mav = new ModelAndView("post_form");
        Post newPost = new Post();
        mav.addObject("post", newPost);
        return mav;
    }
    
    @PostMapping("/savePost")
    public String savePost(@ModelAttribute Post post) {
        postService.save(post);
        return "redirect:/";
    }
}