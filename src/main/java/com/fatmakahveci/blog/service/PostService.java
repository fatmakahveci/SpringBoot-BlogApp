package com.fatmakahveci.blog.service;

import java.util.List;
import java.util.Optional;

import com.fatmakahveci.blog.PostNotFoundException;
import com.fatmakahveci.blog.model.Post;

public interface PostService {
    List<Post> findAll();
    Post findById(Integer id) throws PostNotFoundException;
    Post save(Post post);
    void deleteById(Integer id);
    Optional<Post> findByTitle(String title);
}