package com.fatmakahveci.blog.controller;

import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fatmakahveci.blog.exception.PostNotFoundException;
import com.fatmakahveci.blog.model.Post;
import com.fatmakahveci.blog.model.PostStatus;
import com.fatmakahveci.blog.service.PostService;

@RestController
@RequestMapping("/api/posts")
public class PostRestController {

    private final PostService postService;

    public PostRestController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public PageResponse<Post> getPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "") String query,
            @RequestParam(defaultValue = "newest") String sort,
            Authentication authentication) {
        Page<Post> posts = postService.findAll(
                query,
                PostPageRequest.of(page, size, sort),
                PostVisibility.canViewDrafts(authentication));
        return PageResponse.from(posts);
    }

    @GetMapping("/{slug}")
    public Post getPost(@PathVariable String slug, Authentication authentication) {
        Post post = postService.findBySlug(slug).orElseThrow(() -> new PostNotFoundException(slug));
        if (post.getStatus() != PostStatus.PUBLISHED && !PostVisibility.canViewDrafts(authentication)) {
            throw new PostNotFoundException(slug);
        }
        return post;
    }
}
