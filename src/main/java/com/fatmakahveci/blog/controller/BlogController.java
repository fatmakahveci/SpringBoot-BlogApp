package com.fatmakahveci.blog.controller;

import com.fatmakahveci.blog.BlogNotFoundException;
import com.fatmakahveci.blog.dao.BlogRepository;
import com.fatmakahveci.blog.dao.TagRepository;
import com.fatmakahveci.blog.model.Blog;
import com.fatmakahveci.blog.model.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@RestController
@RequestMapping(path = "/blog")
public class BlogController {
    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private TagRepository tagRepository;

    @PostMapping(path = "/add")
    public Blog addNewBlog(@RequestBody Blog blog) {
        if (blog.getTags() != null) {
            for (Tag tag : blog.getTags()) {
                System.out.println(tagRepository.findByName(tag.getName()));
            }
        }
        return blogRepository.save(blog);
    }

    @GetMapping(path = "/single/{id}")
    public Blog getSingle(@PathVariable Integer id) {
        return blogRepository.findById(id).orElseThrow(() -> new BlogNotFoundException(id));
    }

    @GetMapping(path = "/all")
    public @ResponseBody Iterable<Blog> getAllBlogs() {
        return blogRepository.findAll();
    }
}