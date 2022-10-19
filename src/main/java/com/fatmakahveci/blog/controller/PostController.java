package com.fatmakahveci.blog.controller;

import com.fatmakahveci.blog.exception.PostNotFoundException;
import com.fatmakahveci.blog.model.Post;
import com.fatmakahveci.blog.model.Tag;
import com.fatmakahveci.blog.service.PostService;
import com.fatmakahveci.blog.service.TagService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
public class PostController {

    private PostService postService;
    private TagService tagService;

    @Autowired
    public PostController(PostService postService, TagService tagService) {
        this.postService = postService;
        this.tagService = tagService;
    }

    @GetMapping(path = "/posts")
    public List<Post> getAllUsers() {
        return postService.findAll();
    }

    @GetMapping("/posts/add")
    public ModelAndView addPost() {
        ModelAndView mav = new ModelAndView("post_form");
        mav.addObject("post", new Post());
        mav.addObject("tag", new Tag());
        mav.addObject("tags", tagService.findAll());
        return mav;
    }
    
    @PostMapping(value="/posts")
    public ModelAndView savePost(@ModelAttribute Post post) {
        postService.save(post);
        return new ModelAndView("redirect:/");
    }

    @DeleteMapping("/posts/{id}")
    public ModelAndView deletePost(@PathVariable Integer id) {
        postService.deleteById(id);
        return new ModelAndView("redirect:/");
    }

    @GetMapping("/posts/{id}")
    public ModelAndView editPost(@PathVariable Integer id) {
        Post post = postService.findById(id).orElseThrow(() -> new PostNotFoundException(id));
        ModelAndView mav = new ModelAndView("update_post_form");
        mav.addObject("post", post);
        mav.addObject("tag", new Tag());
        mav.addObject("tags", tagService.findAll());
        return mav;
    }

    @PostMapping("/posts/{id}")
	public ModelAndView updatePost(@PathVariable Integer id, Post post) {
        postService.save(post);
        return new ModelAndView("redirect:/");
	}
}
