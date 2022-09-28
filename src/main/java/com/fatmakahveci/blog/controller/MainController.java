package com.fatmakahveci.blog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

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
}