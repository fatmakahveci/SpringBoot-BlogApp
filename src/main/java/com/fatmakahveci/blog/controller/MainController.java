package com.fatmakahveci.blog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import com.fatmakahveci.blog.model.Tag;
import com.fatmakahveci.blog.service.PostService;
import com.fatmakahveci.blog.service.TagService;

@Controller
public class MainController {
    private PostService postService;
    private TagService tagService;

    @Autowired
    public MainController(PostService postService, TagService tagService) {
        this.postService = postService;
        this.tagService = tagService;
    }

    @GetMapping({"/",""})
    public ModelAndView viewHomePage() {
        ModelAndView mav = new ModelAndView("index");
		mav.addObject("posts", postService.findAll());
        mav.addObject("tag", new Tag());
        mav.addObject("tags", tagService.findAll());
        return mav;
    }
}