package com.fatmakahveci.blog.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import com.fatmakahveci.blog.dao.PostRepository;
import com.fatmakahveci.blog.exception.DuplicatePostTitleException;
import com.fatmakahveci.blog.model.Post;
import com.fatmakahveci.blog.model.PostStatus;
import com.fatmakahveci.blog.service.impl.PostServiceImpl;
import com.fatmakahveci.blog.service.SlugGenerator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static com.fatmakahveci.blog.support.PostFixtures.DEFAULT_TITLE;
import static com.fatmakahveci.blog.support.PostFixtures.aPost;

@ExtendWith(MockitoExtension.class)
class PostServiceTests {

    @Mock
    private PostRepository postRepository;

    @Mock
    private SlugGenerator slugGenerator;

    @InjectMocks
    private PostServiceImpl postService;

	@Test
    void savePostSuccess() {
        Post newPost = aPost().id(null).build();
        Post savedPost = aPost().build();

        when(postRepository.save(eq(newPost))).thenReturn(savedPost);

        Post returnedPost = postService.save(newPost);
        verify(postRepository, times(1)).save(eq(newPost));

        assertNotNull(returnedPost.getId());
        assertThat(returnedPost).isEqualTo(savedPost);
    }

    @Test
    public void deleteByIdSuccess() {
        Post post = aPost().build();

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
        posts.add(aPost().title("first title").content("first content").build());
        posts.add(aPost().id(2).title("second title").content("second content").build());

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
    void paginatedSearchUsesTrimmedTitleQuery() {
        PageRequest pageable = PageRequest.of(0, 10);
        Page<Post> expectedPage = new PageImpl<>(List.of(
                aPost().title("Spring").build()));
        when(postRepository.findByTitleContainingIgnoreCase("spring", pageable)).thenReturn(expectedPage);

        Page<Post> result = postService.findAll("  spring  ", pageable, true);

        assertThat(result).isSameAs(expectedPage);
        verify(postRepository).findByTitleContainingIgnoreCase("spring", pageable);
    }

    @Test
    void blankSearchUsesStandardPagination() {
        PageRequest pageable = PageRequest.of(0, 10);
        Page<Post> expectedPage = Page.empty(pageable);
        when(postRepository.findAll(pageable)).thenReturn(expectedPage);

        Page<Post> result = postService.findAll(" ", pageable, true);

        assertThat(result).isSameAs(expectedPage);
        verify(postRepository).findAll(pageable);
    }

    @Test
    void publicPaginationReturnsOnlyPublishedPosts() {
        PageRequest pageable = PageRequest.of(0, 10);
        Page<Post> expectedPage = Page.empty(pageable);
        when(postRepository.findByStatus(PostStatus.PUBLISHED, pageable)).thenReturn(expectedPage);

        Page<Post> result = postService.findAll("", pageable, false);

        assertThat(result).isSameAs(expectedPage);
        verify(postRepository).findByStatus(PostStatus.PUBLISHED, pageable);
        verify(postRepository, never()).findAll(pageable);
    }

    @Test
    void savingPostGeneratesSlugFromTitle() {
        Post post = aPost().id(null).title("Spring Boot").build();
        when(slugGenerator.fromTitle("Spring Boot")).thenReturn("spring-boot-abc");
        when(postRepository.save(post)).thenReturn(post);

        Post result = postService.save(post);

        assertThat(result.getSlug()).isEqualTo("spring-boot-abc");
    }

    @Test
    public void findByIdSuccess() {
        Post post = aPost().build();

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
        Post post = aPost().build();

        when(postRepository.findByTitle(DEFAULT_TITLE)).thenReturn(Optional.of(post));

        Optional<Post> optionalPost = postService.findByTitle(DEFAULT_TITLE);
        verify(postRepository, times(1)).findByTitle(DEFAULT_TITLE);

        assertEquals(DEFAULT_TITLE, optionalPost.get().getTitle());
    }

    @Test
    public void findByTitleFail() {
        when(postRepository.findByTitle(DEFAULT_TITLE)).thenReturn(Optional.empty());

        Optional<Post> optionalPost = postService.findByTitle(DEFAULT_TITLE);
        verify(postRepository, times(1)).findByTitle(DEFAULT_TITLE);

        assertThat(optionalPost).isEmpty();
    }

    @Test
    void saveUpdatesTheExistingPostWhenIdsMatch() {
        Post existingPost = aPost().content("old content").build();
        Post submittedPost = aPost().content("new content").build();
        when(postRepository.findByTitle(DEFAULT_TITLE)).thenReturn(Optional.of(existingPost));
        when(postRepository.save(existingPost)).thenReturn(existingPost);

        Post result = postService.save(submittedPost);

        assertThat(result.getContent()).isEqualTo("new content");
        verify(postRepository).save(existingPost);
    }

    @Test
    void saveDoesNotOverwriteAnotherPostWithTheSameTitle() {
        Post existingPost = aPost().content("existing content").build();
        Post submittedPost = aPost().id(null).content("submitted content").build();
        when(postRepository.findByTitle(DEFAULT_TITLE)).thenReturn(Optional.of(existingPost));
        org.junit.jupiter.api.Assertions.assertThrows(
                DuplicatePostTitleException.class,
                () -> postService.save(submittedPost));

        assertThat(existingPost.getContent()).isEqualTo("existing content");
        verify(postRepository, never()).save(any());
    }
}
