package com.fatmakahveci.blog.controller;

import com.fatmakahveci.blog.exception.PostNotFoundException;
import com.fatmakahveci.blog.exception.DuplicatePostTitleException;
import com.fatmakahveci.blog.model.Post;
import com.fatmakahveci.blog.model.Tag;
import com.fatmakahveci.blog.service.PostService;
import com.fatmakahveci.blog.service.TagService;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PostController {

    private final PostService postService;
    private final TagService tagService;

    public PostController(PostService postService, TagService tagService) {
        this.postService = postService;
        this.tagService = tagService;
    }

    @GetMapping("/posts/add")
    public ModelAndView addPost() {
        return postForm("post_form", new Post(), null);
    }
    
    @PostMapping("/posts")
    public ModelAndView savePost(
            @Valid @ModelAttribute("post") Post post,
            BindingResult bindingResult) {
        post.setId(null);
        if (bindingResult.hasErrors()) {
            return postForm("post_form", post, bindingResult);
        }

        try {
            postService.save(post);
        } catch (DuplicatePostTitleException exception) {
            bindingResult.rejectValue("title", "post.title.duplicate", exception.getMessage());
            return postForm("post_form", post, bindingResult);
        }
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
        return postForm("update_post_form", post, null);
    }

    @PostMapping("/posts/{id}")
    public ModelAndView updatePost(
            @PathVariable Integer id,
            @Valid @ModelAttribute("post") Post post,
            BindingResult bindingResult) {
        postService.findById(id).orElseThrow(() -> new PostNotFoundException(id));
        post.setId(id);
        if (bindingResult.hasErrors()) {
            return postForm("update_post_form", post, bindingResult);
        }

        try {
            postService.save(post);
        } catch (DuplicatePostTitleException exception) {
            bindingResult.rejectValue("title", "post.title.duplicate", exception.getMessage());
            return postForm("update_post_form", post, bindingResult);
        }
        return new ModelAndView("redirect:/");
    }

    private ModelAndView postForm(String viewName, Post post, BindingResult bindingResult) {
        ModelAndView mav = new ModelAndView(viewName);
        mav.addObject("post", post);
        if (bindingResult != null) {
            mav.addObject(BindingResult.MODEL_KEY_PREFIX + "post", bindingResult);
        }
        mav.addObject("tag", new Tag());
        mav.addObject("tags", tagService.findAll());
        return mav;
    }
}
