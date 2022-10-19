package com.fatmakahveci.blog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import com.fatmakahveci.blog.dao.PostRepository;
import com.fatmakahveci.blog.model.Post;
import com.fatmakahveci.blog.service.TagService;
import com.fatmakahveci.blog.service.impl.PostServiceImpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
    
    @Mock
    private PostRepository postRepository;

    @Mock
    private TagService tagService;

    @InjectMocks
    private PostServiceImpl postService;

	@Test
    public void savePostSuccess() throws Exception {
        Post newPost = new Post(null, "title", "content", Collections.emptySet());
        Post savedPost = new Post(1, "title", "content", Collections.emptySet());

        when(postRepository.save(eq(newPost))).thenReturn(savedPost);

        Post returnedPost = postService.save(newPost);
        verify(postRepository, times(1)).save(eq(newPost));

        assertNotNull(returnedPost.getId());
        assertThat(returnedPost).isEqualTo(savedPost);
    }

    @Test
    public void deleteByIdSuccess() {
        Post post = new Post(1, "title", "content", Collections.emptySet());

        when(postRepository.save(post)).thenReturn(post);
        when(postRepository.findById(1)).thenReturn(Optional.of(post));

        postService.save(post);
        Optional<Post> deletedPost = postService.deleteById(1);
        verify(postRepository, times(1)).save(post);
        verify(postRepository, times(1)).findById(1);

        assertEquals(deletedPost.get(), post);
    }

    @Test
    public void deleteByIdFail_notExistingPost() {
        when(postRepository.findById(1)).thenReturn(Optional.empty());

        postService.deleteById(1);
        verify(postRepository, times(1)).deleteById(1);

        assertThat(postService.findById(1)).isEmpty();
    }

    @Test
    public void findAllSuccess() {
        List<Post> posts = new ArrayList<>();
        posts.add(new Post(1, "first title", "first content", Collections.emptySet()));
        posts.add(new Post(2, "second title", "second content", Collections.emptySet()));

        when(postRepository.findAll()).thenReturn(posts);

        List<Post> actualPosts = postService.findAll();
        verify(postRepository, times(1)).findAll();

        assertEquals(posts, actualPosts);
    }

    @Test
    public void findAllSuccess_emptyPostList() {
        when(postRepository.findAll()).thenReturn(new ArrayList<Post>());

        List<Post> actualPosts = postService.findAll();
        verify(postRepository, times(1)).findAll();

        assertThat(actualPosts).isEmpty();
    }

    @Test
    public void findByIdSuccess() {
        Post post = new Post(1, "title", "content", Collections.emptySet());

        when(postRepository.findById(1)).thenReturn(Optional.of(post));

        Optional<Post> optionalPost = postService.findById(1);
        Post actualPost = optionalPost.get();
        verify(postRepository, times(1)).findById(1);

        assertEquals(post, actualPost);
    }

    @Test
    public void findByIdFail() {
        when(postRepository.findById(2)).thenReturn(Optional.empty());

        Optional<Post> optionalPost = postService.findById(2);
        verify(postRepository, times(1)).findById(2);

        assertThat(optionalPost).isEmpty();
    }

    @Test
    public void findByTitleSuccess() {
        Post post = new Post(1, "title", "content", Collections.emptySet());

        when(postRepository.findByTitle("title")).thenReturn(Optional.of(post));

        Optional<Post> optionalPost = postService.findByTitle("title");
        verify(postRepository, times(1)).findByTitle("title");

        assertEquals("title", optionalPost.get().getTitle());
    }

    @Test
    public void findByTitleFail() {
        when(postRepository.findByTitle("title")).thenReturn(Optional.empty());

        Optional<Post> optionalPost = postService.findByTitle("title");
        verify(postRepository, times(1)).findByTitle("title");

        assertThat(optionalPost).isEmpty();
    }
}
