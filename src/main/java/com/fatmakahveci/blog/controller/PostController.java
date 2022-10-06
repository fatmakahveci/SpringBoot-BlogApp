package com.fatmakahveci.blog.controller;

import com.fatmakahveci.blog.PostNotFoundException;
import com.fatmakahveci.blog.model.Post;
import com.fatmakahveci.blog.model.Tag;
import com.fatmakahveci.blog.service.PostService;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
public class PostController {

    private PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping(path = "/posts")
    public List<Post> getAllUsers() {
        return postService.findAll();
    }

    @GetMapping(path = "/posts/{id}")
    public Post viewPostById(@PathVariable Integer id) {
        return postService.findById(id).orElseThrow(() -> new PostNotFoundException(id));
    }

    @GetMapping("/posts/add")
    public ModelAndView addPost() {
        ModelAndView mav = new ModelAndView("post_form");
        mav.addObject("post", new Post());
        mav.addObject("tag", new Tag());
        return mav;
    }
    
    @PostMapping(value="/posts/save")
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

    @GetMapping("/posts/delete/{id}")
    public ModelAndView deletePost(@PathVariable Integer id) {
        postService.deleteById(id);
        return new ModelAndView("redirect:/");
    }

    @GetMapping("/posts/edit/{id}")
    public ModelAndView editPost(@PathVariable Integer id) {
        Post post = postService.findById(id).orElseThrow(() -> new PostNotFoundException(id));
        ModelAndView mav = new ModelAndView("update_post_form");
        mav.addObject("post", post);
        mav.addObject("tag", new Tag());
        return mav;
    }

    @PostMapping("/posts/update/{id}")
	public ModelAndView updatePost(@PathVariable Integer id, Post post) {
        Post existingPost = postService.findById(id).orElseThrow(() -> new PostNotFoundException(id));
        existingPost.setTitle(post.getTitle());
        existingPost.setContent(post.getContent());
        existingPost.setTags(post.getTags());
        postService.save(existingPost);
        return new ModelAndView("redirect:/");
	}
}