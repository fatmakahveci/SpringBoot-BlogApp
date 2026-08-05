package com.fatmakahveci.blog.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.fatmakahveci.blog.dao.PostRepository;
import com.fatmakahveci.blog.exception.DuplicatePostTitleException;
import com.fatmakahveci.blog.model.Post;
import com.fatmakahveci.blog.model.PostStatus;
import com.fatmakahveci.blog.service.PostService;
import com.fatmakahveci.blog.service.SlugGenerator;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final SlugGenerator slugGenerator;

    public PostServiceImpl(PostRepository postRepository, SlugGenerator slugGenerator) {
        this.postRepository = postRepository;
        this.slugGenerator = slugGenerator;
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "seo-sitemap", allEntries = true)
    public Post save(Post post) {
        post.setSlug(slugGenerator.fromTitle(post.getTitle()));
        Optional<Post> postWithTitle = findByTitle(post.getTitle());
        if (postWithTitle.isPresent()) {
            Post existingPost = postWithTitle.get();
            if (post.getId() == null || !existingPost.getId().equals(post.getId())) {
                throw new DuplicatePostTitleException(post.getTitle());
            }
            return postRepository.save(update(existingPost, post));
        }

        return postRepository.save(post);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Post> findById(Integer id) {
        return postRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Post> findAll() {
        return postRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Post> findPublished() {
        return postRepository.findByStatusOrderByUpdatedAtDesc(PostStatus.PUBLISHED);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Post> findAll(String query, Pageable pageable, boolean includeDrafts) {
        String normalizedQuery = query == null ? "" : query.trim();
        if (includeDrafts && normalizedQuery.isEmpty()) {
            return initializeTags(postRepository.findAll(pageable));
        }
        if (includeDrafts) {
            return initializeTags(postRepository.findByTitleContainingIgnoreCase(normalizedQuery, pageable));
        }
        if (normalizedQuery.isEmpty()) {
            return initializeTags(postRepository.findByStatus(PostStatus.PUBLISHED, pageable));
        }
        return initializeTags(postRepository.findByStatusAndTitleContainingIgnoreCase(
                PostStatus.PUBLISHED, normalizedQuery, pageable));
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "seo-sitemap", allEntries = true)
    public Optional<Post> deleteById(Integer id) {
        Optional<Post> post = postRepository.findById(id);
        postRepository.deleteById(id);
        return post;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Post> findByTitle(String title) {
        return postRepository.findByTitle(title);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Post> findBySlug(String slug) {
        return postRepository.findBySlug(slug);
    }

    private Post update(Post existingPost, Post submittedPost) {
        existingPost.setTitle(submittedPost.getTitle());
        existingPost.setContent(submittedPost.getContent());
        existingPost.setSummary(submittedPost.getSummary());
        existingPost.setSlug(submittedPost.getSlug());
        existingPost.setStatus(submittedPost.getStatus());
        existingPost.setTags(submittedPost.getTags());
        return existingPost;
    }

    private Page<Post> initializeTags(Page<Post> posts) {
        posts.forEach(post -> post.getTags().size());
        return posts;
    }
}
