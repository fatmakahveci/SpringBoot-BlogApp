package com.fatmakahveci.blog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;

import com.fatmakahveci.blog.model.Tag;
import com.fatmakahveci.blog.model.Post;
import com.fatmakahveci.blog.model.PostStatus;
import com.fatmakahveci.blog.exception.PostNotFoundException;
import com.fatmakahveci.blog.service.PostService;
import com.fatmakahveci.blog.service.TagService;

@Controller
public class MainController {
    private final PostService postService;
    private final TagService tagService;

    public MainController(PostService postService, TagService tagService) {
        this.postService = postService;
        this.tagService = tagService;
    }

    @GetMapping({"/", ""})
    public ModelAndView viewHomePage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String query,
            @RequestParam(defaultValue = "newest") String sort,
            Authentication authentication) {
        var pageable = PostPageRequest.of(page, size, sort);
        Page<Post> postPage = postService.findAll(query, pageable, PostVisibility.canViewDrafts(authentication));
        ModelAndView mav = new ModelAndView("index");
        mav.addObject("posts", postPage.getContent());
        mav.addObject("postPage", postPage);
        mav.addObject("query", query.trim());
        mav.addObject("sort", PostPageRequest.normalizeSort(sort));
        mav.addObject("size", pageable.getPageSize());
        mav.addObject("seoNoIndex", page > 0 || !query.isBlank() || !"newest".equals(sort));
        mav.addObject("tag", new Tag());
        mav.addObject("tags", tagService.findAll());
        return mav;
    }

    @GetMapping("/p/{slug}")
    public ModelAndView viewPost(@PathVariable String slug, Authentication authentication) {
        Post post = postService.findBySlug(slug).orElseThrow(() -> new PostNotFoundException(slug));
        if (post.getStatus() != PostStatus.PUBLISHED && !PostVisibility.canViewDrafts(authentication)) {
            throw new PostNotFoundException(slug);
        }
        ModelAndView mav = new ModelAndView("post_detail", "post", post);
        mav.addObject("seoNoIndex", post.getStatus() != PostStatus.PUBLISHED);
        return mav;
    }
}
