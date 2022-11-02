package com.fatmakahveci.blog.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fatmakahveci.blog.dao.PostRepository;
import com.fatmakahveci.blog.model.Post;
import com.fatmakahveci.blog.service.PostService;

@Service
public class PostServiceImpl implements PostService {

    private PostRepository postRepository;

    @Autowired
    public PostServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    public Post save(Post post) {
        Optional<Post> optionalPost = findByTitle(post.getTitle());
        if (optionalPost.isPresent()) {
            Post existingPost = optionalPost.get();
            existingPost.setTitle(post.getTitle());
            existingPost.setContent(post.getContent());
            existingPost.setTags(post.getTags());
            post = existingPost;
        }

        return postRepository.save(post);
    }

    @Override
    public List<Post> saveAll(List<Post> postList) {
		List<Post> posts = (List<Post>) postRepository.saveAll(postList);
		return posts;
	}

    @Override
    public Optional<Post> findById(Integer id) {
        return postRepository.findById(id);
    }

    @Override
    public List<Post> findAll() {
        return postRepository.findAll();
    }

    @Override
    public Optional<Post> deleteById(Integer id) {
        Optional<Post> post = postRepository.findById(id);
        postRepository.deleteById(id);
        return post;
    }

    @Override
    public List<Post> deleteAll() {
        List<Post> posts = postRepository.findAll();
        postRepository.deleteAll();
        return posts;
    }

    @Override
    public Optional<Post> findByTitle(String title) {
        return postRepository.findByTitle(title);
    }
}