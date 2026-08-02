package com.fatmakahveci.blog.controller;

import com.fatmakahveci.blog.model.Tag;
import com.fatmakahveci.blog.exception.TagNotFoundException;
import com.fatmakahveci.blog.exception.DuplicateTagNameException;
import com.fatmakahveci.blog.service.PostService;
import com.fatmakahveci.blog.service.TagService;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.security.core.Authentication;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class TagController {

    private final TagService tagService;
    private final PostService postService;

    public TagController(TagService tagService, PostService postService) {
        this.tagService = tagService;
        this.postService = postService;
    }

    @GetMapping(path = "/tags/{id}")
    public ModelAndView getTagPosts(@PathVariable Integer id, Authentication authentication) {
        ModelAndView mav = new ModelAndView("tag");
        Tag tag = tagService.findById(id).orElseThrow(() -> new TagNotFoundException(id));
        mav.addObject("tag", tag);
        mav.addObject("posts", tag.getPosts().stream()
                .filter(post -> post.getStatus() == com.fatmakahveci.blog.model.PostStatus.PUBLISHED
                        || PostVisibility.canViewDrafts(authentication))
                .toList());
        return mav;
    }

    @PostMapping("/tags")
    public ModelAndView saveTag(
            @Valid @ModelAttribute("tag") Tag tag,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return homeWithTagErrors(tag, bindingResult);
        }

        try {
            tagService.createByName(tag.getName());
        } catch (DuplicateTagNameException exception) {
            bindingResult.rejectValue("name", "tag.name.duplicate", exception.getMessage());
            return homeWithTagErrors(tag, bindingResult);
        }
        return new ModelAndView("redirect:/");
    }

    @DeleteMapping("/tags/{id}")
    public ModelAndView deleteTag(@PathVariable Integer id) {
       tagService.deleteById(id);
       return new ModelAndView("redirect:/");
    }

    private ModelAndView homeWithTagErrors(Tag tag, BindingResult bindingResult) {
        ModelAndView mav = new ModelAndView("index");
        mav.addObject("tag", tag);
        mav.addObject(BindingResult.MODEL_KEY_PREFIX + "tag", bindingResult);
        mav.addObject("tags", tagService.findAll());
        mav.addObject("posts", postService.findAll());
        return mav;
    }
}
