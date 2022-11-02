package com.fatmakahveci.blog.service;

import java.util.List;
import java.util.Optional;

import com.fatmakahveci.blog.model.Tag;

public interface TagService {
    List<Tag> findAll();
    Optional<Tag> findById(Integer id);
    Optional<Tag> findByName(String name);
    Tag save(Tag tag);
    List<Tag> saveAll(List<Tag> tagList);
    Tag getOrCreateByName(String name);
    void deleteById(Integer id);
}
