package com.fatmakahveci.blog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.fatmakahveci.blog.TagNotFoundException;
import com.fatmakahveci.blog.dao.TagRepository;
import com.fatmakahveci.blog.model.Tag;

@Service
public class ITagService implements TagService {

    private TagRepository tagRepository;

    @Autowired
    public ITagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }
    
    @Override
    public Tag save(@RequestBody Tag tag) {
        return tagRepository.save(tag);
    }

    @Override
    public Tag findById(@PathVariable Integer id) {
        return tagRepository.findById(id).orElseThrow(() -> new TagNotFoundException(id));
    }

    @Override
    public Iterable<Tag> findAll(){
        return tagRepository.findAll();
    }
}
