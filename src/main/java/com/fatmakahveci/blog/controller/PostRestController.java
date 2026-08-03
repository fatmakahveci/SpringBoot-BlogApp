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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/posts")
@Tag(name = "Posts", description = "Browse published posts")
public class PostRestController {

    private final PostService postService;

    public PostRestController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    @Operation(summary = "List posts", description = "Returns a searchable and sortable page of posts. Anonymous users only see published posts.")
    @ApiResponse(responseCode = "200", description = "Post page returned")
    public PageResponse<Post> getPosts(
            @Parameter(description = "Zero-based page number", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size from 1 to 50", example = "20")
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Case-insensitive title search")
            @RequestParam(defaultValue = "") String query,
            @Parameter(description = "Sort mode", example = "newest")
            @RequestParam(defaultValue = "newest") String sort,
            @Parameter(hidden = true)
            Authentication authentication) {
        Page<Post> posts = postService.findAll(
                query,
                PostPageRequest.of(page, size, sort),
                PostVisibility.canViewDrafts(authentication));
        return PageResponse.from(posts);
    }

    @GetMapping("/{slug}")
    @Operation(summary = "Get a post by slug")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Post returned"),
            @ApiResponse(responseCode = "404", description = "Post not found")
    })
    public Post getPost(
            @Parameter(description = "Stable post slug", example = "getting-started") @PathVariable String slug,
            @Parameter(hidden = true) Authentication authentication) {
        Post post = postService.findBySlug(slug).orElseThrow(() -> new PostNotFoundException(slug));
        if (post.getStatus() != PostStatus.PUBLISHED && !PostVisibility.canViewDrafts(authentication)) {
            throw new PostNotFoundException(slug);
        }
        return post;
    }
}
