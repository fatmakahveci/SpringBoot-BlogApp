package com.fatmakahveci.blog.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fatmakahveci.blog.PostNotFoundException;
import com.fatmakahveci.blog.dao.PostRepository;
import com.fatmakahveci.blog.model.Post;
import com.fatmakahveci.blog.model.Tag;
import com.fatmakahveci.blog.service.PostService;
import com.fatmakahveci.blog.service.TagService;

@Service
public class PostServiceImpl implements PostService {

    private PostRepository postRepository;

    private TagService tagService;

    @Autowired
    public PostServiceImpl(PostRepository postRepository, TagService tagService) {
        this.postRepository = postRepository;
        this.tagService = tagService;
    }

    @Override
    public Post save(Post post) {
        List<Tag> tags = new ArrayList<>();
        for (Tag tag : post.getTags()) {
            tags.add(tagService.getOrCreateByName(tag.getName()));
        }
        post.setTags(tags);
        return postRepository.save(post);
    }

    @Override
    public Optional<Post> findById(Integer id) throws PostNotFoundException {
        return postRepository.findById(id);
    }

    @Override
    public List<Post> findAll() {
        return postRepository.findAll();
    }

    @Override
    public void deleteById(Integer id) {
        postRepository.deleteById(id);
    }

    @Override
    public Optional<Post> findByTitle(String title) {
        return postRepository.findByTitle(title);
    }
}