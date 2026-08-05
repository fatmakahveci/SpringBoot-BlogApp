package com.fatmakahveci.blog.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.fatmakahveci.blog.model.Post;
import com.fatmakahveci.blog.model.PostStatus;

public interface PostRepository extends JpaRepository<Post, Integer> {
    @Override
    @EntityGraph(attributePaths = "tags")
    Optional<Post> findById(Integer id);

    @Override
    @EntityGraph(attributePaths = "tags")
    List<Post> findAll();

    @EntityGraph(attributePaths = "tags")
    Optional<Post> findByTitle(String title);
    @EntityGraph(attributePaths = "tags")
    Optional<Post> findBySlug(String slug);
    Page<Post> findByTitleContainingIgnoreCase(String query, Pageable pageable);
    Page<Post> findByStatus(PostStatus status, Pageable pageable);
    Page<Post> findByStatusAndTitleContainingIgnoreCase(PostStatus status, String query, Pageable pageable);
    @EntityGraph(attributePaths = "tags")
    List<Post> findByStatusOrderByUpdatedAtDesc(PostStatus status);
}
