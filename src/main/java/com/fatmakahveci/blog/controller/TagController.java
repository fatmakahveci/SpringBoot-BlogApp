package com.fatmakahveci.blog.controller;

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
    public void addTag(@RequestBody Tag tag) {
        tagService.save(tag);
    }

    @GetMapping
    public @ResponseBody List<Tag> getAllTags() {
        return tagService.findAll();
    }

    @GetMapping(path = "/{id}")
    public Tag findById(@PathVariable Integer id) {
        return tagService.findById(id);
    }

    @GetMapping(path = "/tag")
    public Tag findByName(@RequestParam(name="name") String name) {
        return tagService.findByName(name);
    }

    @GetMapping("/add")
    public ModelAndView addTag() {
        ModelAndView mav = new ModelAndView("tag_form");
        mav.addObject("tag", new Tag());
        return mav;
    }
    
    @PostMapping("/save")
    public ModelAndView saveTag(@ModelAttribute Tag tag) {
        tagService.save(tag);
        return new ModelAndView("redirect:/");
    }

    @GetMapping("/delete/{id}")
    public ModelAndView deleteTag(@PathVariable Integer id) {
        tagService.deleteById(id);
        return new ModelAndView("redirect:/");
    }
}