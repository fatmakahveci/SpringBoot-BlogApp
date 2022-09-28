package com.fatmakahveci.blog.controller;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import com.fatmakahveci.blog.model.Post;
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
        var posts = postService.findAll();
        var params = new HashMap<String, List<Post>>();
        params.put("posts", posts);
        return new ModelAndView("index", params);
    }

    @GetMapping(path = "/post")
    public String postForm(Model model) {
        model.addAttribute("post", new Post());
        return "post";
    }

    @PostMapping("/post")
    public String postSubmit(@ModelAttribute Post post, Model model) {
      model.addAttribute("post", post);
      postService.save(post);
      return "post";
    }

    @GetMapping(path = "/tag")
    public String tagForm(Model model) {
        model.addAttribute("tag", new Tag());
        return "tag";
    }

    @PostMapping("/tag")
    public String tagSubmit(@ModelAttribute Tag tag, Model model) {
      model.addAttribute("tag", tag);
      tagService.save(tag);
      return "tag";
    }
}