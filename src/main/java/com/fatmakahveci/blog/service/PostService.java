package com.fatmakahveci.blog.service;

import java.util.List;
import java.util.Optional;

import com.fatmakahveci.blog.model.Post;

public interface PostService {
    List<Post> findAll();
    Optional<Post> findById(Integer id);
    Post save(Post post);
    List<Post> saveAll(List<Post> posts);
    Optional<Post> deleteById(Integer id);
    List<Post> deleteAll();
    Optional<Post> findByTitle(String title);
}