package com.fatmakahveci.blog.controller;

import com.fatmakahveci.blog.model.Tag;
import com.fatmakahveci.blog.service.TagService;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
public class TagController {
    private TagService tagService;

    @Autowired
    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping(path = "/tags")
    public List<Tag> getAllTags() {
        return tagService.findAll();
    }

    @PostMapping(value="/tags/save")
    public ModelAndView saveTag(@ModelAttribute Tag tag) {
        Tag newTag = tagService.getOrCreateByName(tag.getName());
        tagService.save(newTag);
        return new ModelAndView("redirect:/posts/add");
    }

    @GetMapping(value="/tags/delete")
    public ModelAndView deleteTag(@ModelAttribute Tag tag) {
        Optional<Tag> optionalTag = tagService.findByName(tag.getName());
        if (optionalTag.isPresent()) {
            Tag newTag = optionalTag.get();
            tagService.deleteById(newTag.getId());
        }
        return new ModelAndView("redirect:/posts/add");
    }

    @PostMapping(value="/tags/delete")
    public ModelAndView removeTag(@ModelAttribute Tag tag) {
        Optional<Tag> optionalTag = tagService.findByName(tag.getName());
        if (optionalTag.isPresent()) {
            Tag newTag = optionalTag.get();
            tagService.deleteById(newTag.getId());
        }
        return new ModelAndView("redirect:/posts/add");
    }
}
