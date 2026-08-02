package com.fatmakahveci.blog.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.fatmakahveci.blog.model.Post;

public interface PostService {
    List<Post> findAll();
    Page<Post> findAll(String query, Pageable pageable, boolean includeDrafts);
    Optional<Post> findById(Integer id);
    Post save(Post post);
    Optional<Post> deleteById(Integer id);
    Optional<Post> findByTitle(String title);
    Optional<Post> findBySlug(String slug);
}
