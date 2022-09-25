package com.fatmakahveci.blog.service;

import com.fatmakahveci.blog.PostNotFoundException;
import com.fatmakahveci.blog.model.Post;

public interface PostService {
    Iterable<Post> findAll();
    Post findById(Integer id) throws PostNotFoundException;
    Post save(Post post);
}
