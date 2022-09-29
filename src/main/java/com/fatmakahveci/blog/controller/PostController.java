package com.fatmakahveci.blog.controller;

import com.fatmakahveci.blog.model.Post;
import com.fatmakahveci.blog.model.Tag;
import com.fatmakahveci.blog.service.PostService;

import java.util.ArrayList;
import java.util.Optional;

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

    @GetMapping("/add")
    public ModelAndView addPost() {
        ModelAndView mav = new ModelAndView("post_form");
        mav.addObject("post", new Post());
        mav.addObject("tags", new ArrayList<Tag>());
        return mav;
    }
    
    @PostMapping("/save")
    public ModelAndView savePost(@ModelAttribute Post post) {
        Optional<Post> optionalPost = postService.findByTitle(post.getTitle());
        if (!optionalPost.isPresent()) {
            postService.save(post);
        } else {
            Post newPost = optionalPost.get();
            newPost.setTitle(post.getTitle());
            newPost.setContent(post.getContent());
            newPost.setTags(post.getTags());
            postService.save(newPost);
        }
        return new ModelAndView("redirect:/");
    }

    @GetMapping("/delete/{id}")
    public ModelAndView deletePost(@PathVariable Integer id) {
        postService.deleteById(id);
        return new ModelAndView("redirect:/");
    }

    @GetMapping("/edit/{id}")
    public ModelAndView editPost(@PathVariable Integer id) {
        Post post = postService.findById(id);
        ModelAndView mav = new ModelAndView("update_post_form");
        mav.addObject("post", post);
        return mav;
    }

    @PostMapping("/update/{id}")
	public ModelAndView updatePost(@PathVariable Integer id, Post post) {
        Post existingPost = postService.findById(id);
        existingPost.setTitle(post.getTitle());
        existingPost.setContent(post.getContent());
        existingPost.setTags(post.getTags());
        postService.save(existingPost);
        return new ModelAndView("redirect:/");
	}
}