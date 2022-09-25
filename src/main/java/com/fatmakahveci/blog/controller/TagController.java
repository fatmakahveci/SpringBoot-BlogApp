package com.fatmakahveci.blog.controller;

import com.fatmakahveci.blog.model.Tag;
import com.fatmakahveci.blog.service.TagService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@RestController
@RequestMapping(path = "/tags")
public class TagController {
    private TagService tagService;

    @Autowired
    public TagController(TagService tagService) {
        this.tagService = tagService;
    }
    
    @PostMapping
    public Tag addNewTag(@RequestBody Tag tag) {
        return tagService.save(tag);
    }

    @GetMapping
    public @ResponseBody Iterable<Tag> getAllTags() {
        return tagService.findAll();
    }

    @GetMapping(path = "/{id}")
    public Tag findById(@PathVariable Integer id) {
        return tagService.findById(id);
    }

    @GetMapping(path = "/tag")
    public String getNameById(@RequestParam(name="id") Integer id) {
        return tagService.findNameById(id);
    }
}