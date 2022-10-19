package com.fatmakahveci.blog.service;

import java.util.List;
import java.util.Optional;

import com.fatmakahveci.blog.model.Post;

public interface PostService {
    List<Post> findAll();
    Optional<Post> findById(Integer id);
    Post save(Post post);
    Optional<Post> deleteById(Integer id);
    Optional<Post> findByTitle(String title);
}