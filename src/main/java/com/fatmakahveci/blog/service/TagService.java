package com.fatmakahveci.blog.service;

import com.fatmakahveci.blog.TagNotFoundException;
import com.fatmakahveci.blog.model.Tag;

public interface TagService {
    Iterable<Tag> findAll();
    Tag findById(Integer id) throws TagNotFoundException;
    Tag save(Tag tag);
    Tag findTagByName(String name);
    Tag getOrCreateTagByName(String name);
}
