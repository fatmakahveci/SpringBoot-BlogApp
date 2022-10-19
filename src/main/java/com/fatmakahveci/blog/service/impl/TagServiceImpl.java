package com.fatmakahveci.blog.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        return tagRepository.save(tag);
    }

    @Override
    public Optional<Tag> findById(Integer id) {
        return tagRepository.findById(id);
    }

    @Override
    public List<Tag> findAll(){
        return tagRepository.findAll();
    }

    @Override
    public Optional<Tag> findByName(String name) {
        return tagRepository.findByName(name);
    }

    @Override 
    public Tag getOrCreateByName(String name) {
        Optional<Tag> optionalTag = tagRepository.findByName(name);
        Tag tag;
        if (optionalTag.isPresent()) {
            tag = optionalTag.get();
        } else {
            tag = new Tag();
            tag.setName(name);
        }
        return tag;
    }

    @Override
    public Optional<Tag> deleteById(Integer id) {
        Optional<Tag> tag = tagRepository.findById(id);
        tagRepository.deleteById(id);
        return tag;
    }
}
