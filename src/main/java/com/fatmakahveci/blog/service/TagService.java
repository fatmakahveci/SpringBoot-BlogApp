package com.fatmakahveci.blog.service;

import java.util.List;
import java.util.Optional;

import com.fatmakahveci.blog.model.Tag;
import com.fatmakahveci.blog.model.PostStatus;

public interface TagService {
    List<Tag> findAll();
    List<Tag> findByPostStatus(PostStatus status);
    Optional<Tag> findById(Integer id);
    Optional<Tag> findByName(String name);
    Tag save(Tag tag);
    Tag createByName(String name);
    void deleteById(Integer id);
}
