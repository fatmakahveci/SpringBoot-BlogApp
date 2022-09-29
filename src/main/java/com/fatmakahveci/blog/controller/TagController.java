package com.fatmakahveci.blog.controller;

import com.fatmakahveci.blog.model.Tag;
import com.fatmakahveci.blog.service.TagService;

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
    public @ResponseBody Iterable<Tag> getAllTags() {
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

    // TODO (fatmakahveci) : Update will not be over form, correct it
    // @GetMapping("/edit/{id}")
    // public ModelAndView editTag(@PathVariable Integer id) {
    //     ModelAndView mav = new ModelAndView("update_tag_form");
    //     mav.addObject("tag", new Tag());
    //     return mav;
    // }

    // TODO (fatmakahveci) : Update will not be over form, correct it
    // @PostMapping("/update/{id}")
    // public ModelAndView updateTag(@PathVariable Integer id) {
    //     ModelAndView mav = new ModelAndView("update_tag_form");
    //     mav.addObject("tag", new Tag());
    //     return mav;
    // }

    @GetMapping("/delete/{id}")
    public String deleteTag(@PathVariable Integer id) {
        tagService.deleteById(id);
        return "redirect:/";
    }
}