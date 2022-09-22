package com.fatmakahveci.blog.controller;

import com.fatmakahveci.blog.TagNotFoundException;
import com.fatmakahveci.blog.dao.TagRepository;
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
@RequestMapping(path = "/tag")
public class TagController {
    @Autowired
    private TagRepository tagRepository;

    @PostMapping(path = "/add")
    public Tag addNewTag(@RequestBody Tag tag) {
        return tagRepository.save(tag);
    }

    @GetMapping(path = "/single/{id}")
    public Tag getSingle(@PathVariable Integer id) {
        return tagRepository.findById(id).orElseThrow(() -> new TagNotFoundException(id));
    }

    @GetMapping(path = "/all")
    public @ResponseBody Iterable<Tag> getAllTags() {
        return tagRepository.findAll();
    }
}