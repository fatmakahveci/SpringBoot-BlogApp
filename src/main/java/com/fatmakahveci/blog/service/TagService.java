package com.fatmakahveci.blog.service;

import java.util.List;

import com.fatmakahveci.blog.TagNotFoundException;
import com.fatmakahveci.blog.model.Tag;

public interface TagService {
    List<Tag> findAll();
    Tag findById(Integer id) throws TagNotFoundException;
    Tag save(Tag tag);
    Tag findTagByName(String name);
    Tag getOrCreateTagByName(String name);
}
