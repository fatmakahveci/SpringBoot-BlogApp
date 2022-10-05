package com.fatmakahveci.blog.controller;

import com.fatmakahveci.blog.TagNotFoundException;
import com.fatmakahveci.blog.model.Tag;
import com.fatmakahveci.blog.service.TagService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@RestController
public class TagController {
    private TagService tagService;

    @Autowired
    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping(path = "/tags")
    public @ResponseBody List<Tag> getAllTags() {
        return tagService.findAll();
    }

    @GetMapping(path = "/tags/{id}")
    public Tag findById(@PathVariable Integer id) {
        return tagService.findById(id).orElseThrow(() -> new TagNotFoundException(id));
    }

    @GetMapping("/tags/add")
    public ModelAndView addTag() {
        ModelAndView mav = new ModelAndView("tag_form");
        mav.addObject("tag", new Tag());
        return mav;
    }
    
    @PostMapping("/tags/save")
    public ModelAndView saveTag(@ModelAttribute Tag tag) {
        tagService.save(tag);
        return new ModelAndView("redirect:/");
    }

    @GetMapping("/tags/delete/{id}")
    public ModelAndView deleteTag(@PathVariable Integer id) {
        tagService.deleteById(id);
        return new ModelAndView("redirect:/");
    }
}