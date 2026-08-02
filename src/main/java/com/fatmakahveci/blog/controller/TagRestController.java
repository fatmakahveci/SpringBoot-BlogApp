package com.fatmakahveci.blog.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fatmakahveci.blog.model.Tag;
import com.fatmakahveci.blog.service.TagService;

@RestController
@RequestMapping("/api/tags")
public class TagRestController {

    private final TagService tagService;

    public TagRestController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping
    public List<Tag> getTags() {
        return tagService.findAll();
    }
}
