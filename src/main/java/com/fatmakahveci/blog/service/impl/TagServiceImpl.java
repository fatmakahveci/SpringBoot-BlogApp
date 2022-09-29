package com.fatmakahveci.blog.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fatmakahveci.blog.TagNotFoundException;
import com.fatmakahveci.blog.dao.TagRepository;
import com.fatmakahveci.blog.model.Tag;
import com.fatmakahveci.blog.service.TagService;

@Service
public class TagServiceImpl implements TagService {

    private TagRepository tagRepository;

    @Autowired
    public TagServiceImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }
    
    @Override
    public Tag save(Tag tag) {
        tag.setId(null);
        return tagRepository.save(tag);
    }

    @Override
    public Tag findById(Integer id) {
        return tagRepository.findById(id).orElseThrow(() -> new TagNotFoundException(id));
    }

    @Override
    public List<Tag> findAll(){
        return tagRepository.findAll();
    }

    @Override
    public Tag findByName(String name) {
        return tagRepository.findByName(name);
    }

    @Override 
    public Tag getOrCreateByName(String name) {
        Tag tag = tagRepository.findByName(name);
        if (tag != null) {
            return tag;
        }
        tag = new Tag();
        tag.setName(name);
        return tagRepository.save(tag);
    }

    @Override
    public void deleteById(Integer id) {
        tagRepository.deleteById(id);
    }
}
