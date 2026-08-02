package com.fatmakahveci.blog.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.fatmakahveci.blog.model.Post;
import com.fatmakahveci.blog.model.PostStatus;

public interface PostRepository extends JpaRepository<Post, Integer> {
    Optional<Post> findByTitle(String title);
    Optional<Post> findBySlug(String slug);
    Page<Post> findByTitleContainingIgnoreCase(String query, Pageable pageable);
    Page<Post> findByStatus(PostStatus status, Pageable pageable);
    Page<Post> findByStatusAndTitleContainingIgnoreCase(PostStatus status, String query, Pageable pageable);
}
