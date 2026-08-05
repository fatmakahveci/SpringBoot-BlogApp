package com.fatmakahveci.blog.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import com.fatmakahveci.blog.model.Post;
import com.fatmakahveci.blog.model.PostStatus;
import com.fatmakahveci.blog.model.Tag;
import com.fatmakahveci.blog.service.PostService;
import com.fatmakahveci.blog.service.TagService;

class DevelopmentSampleDataConfigTests {

    @Test
    void createsPublishedSamplePostsAndTheirTopics() throws Exception {
        PostService postService = mock(PostService.class);
        TagService tagService = mock(TagService.class);
        List<Tag> createdTags = new ArrayList<>();
        when(tagService.findByName(any())).thenReturn(Optional.empty());
        when(tagService.createByName(any())).thenAnswer(invocation -> {
            Tag tag = new Tag();
            tag.setName(invocation.getArgument(0));
            createdTags.add(tag);
            return tag;
        });
        when(postService.findByTitle(any())).thenReturn(Optional.empty());

        new DevelopmentSampleDataConfig().seed(postService, tagService);

        ArgumentCaptor<Post> posts = ArgumentCaptor.forClass(Post.class);
        verify(postService, times(3)).save(posts.capture());
        assertThat(posts.getAllValues())
                .allSatisfy(post -> {
                    assertThat(post.getStatus()).isEqualTo(PostStatus.PUBLISHED);
                    assertThat(post.getSummary()).isNotBlank();
                    assertThat(post.getContent()).isNotBlank();
                    assertThat(post.getTags()).isNotEmpty();
                });
        assertThat(createdTags).extracting(Tag::getName)
                .containsExactlyInAnyOrder("Spring Boot", "Getting Started", "Security", "Testing");
    }

    @Test
    void doesNotDuplicateExistingSampleContent() throws Exception {
        PostService postService = mock(PostService.class);
        TagService tagService = mock(TagService.class);
        Tag existingTag = new Tag();
        existingTag.setName("Existing topic");
        when(tagService.findByName(any())).thenReturn(Optional.of(existingTag));
        when(postService.findByTitle(any())).thenReturn(Optional.of(new Post()));

        new DevelopmentSampleDataConfig().seed(postService, tagService);

        verify(tagService, never()).createByName(any());
        verify(postService, never()).save(any());
    }
}
