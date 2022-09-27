package com.fatmakahveci.blog;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;

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
    public void saveTest() throws Exception {
        Post newPost = new Post(null, "title", "content", Collections.emptyList());
        Post savedPost = new Post(1, "title", "content", Collections.emptyList());
        when(postRepository.save(eq(newPost))).thenReturn(savedPost);

        Post returnedPost = postService.save(newPost);

        verify(postRepository, times(1)).save(eq(newPost));
        assertNotNull(returnedPost.getId());
        
        assertThat(returnedPost).isEqualTo(savedPost);
    }

    // TODO (fatmakahveci) : write test for post with tags
    // TODO (fatmakahveci) : post with given id will be null and test for id given by db
}
