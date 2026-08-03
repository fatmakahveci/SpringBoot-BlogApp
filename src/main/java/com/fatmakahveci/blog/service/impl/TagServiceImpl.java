package com.fatmakahveci.blog.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatmakahveci.blog.dao.TagRepository;
import com.fatmakahveci.blog.exception.DuplicateTagNameException;
import com.fatmakahveci.blog.model.Tag;
import com.fatmakahveci.blog.model.PostStatus;
import com.fatmakahveci.blog.service.TagService;

@Service
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    public TagServiceImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    @Transactional
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
    public List<Tag> findByPostStatus(PostStatus status) {
        return tagRepository.findDistinctByPostsStatus(status);
    }

    @Override
    public Optional<Tag> findByName(String name) {
        return tagRepository.findByName(name);
    }

    @Override
    @Transactional
    public Tag createByName(String name) {
        if (tagRepository.findByName(name).isPresent()) {
            throw new DuplicateTagNameException(name);
        }

        Tag tag = new Tag();
        tag.setName(name);
        return save(tag);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        tagRepository.findById(id).ifPresent(tag -> {
            // Post owns the join table, so detach the tag from every post before deletion.
            tag.deleteTagFromPosts();
            tagRepository.deleteById(id);
        });
    }
}
