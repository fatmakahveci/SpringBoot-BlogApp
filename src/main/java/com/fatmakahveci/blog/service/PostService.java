package com.fatmakahveci.blog.service;

import org.springframework.web.bind.annotation.RequestBody;

import com.fatmakahveci.blog.PostNotFoundException;
import com.fatmakahveci.blog.model.Post;

public interface PostService {
    Iterable<Post> findAll();
    Post findById(Integer id) throws PostNotFoundException;
    Post save(@RequestBody Post post);
}
