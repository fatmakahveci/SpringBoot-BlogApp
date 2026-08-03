package com.fatmakahveci.blog.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;

import com.fatmakahveci.blog.dao.PostRepository;
import com.fatmakahveci.blog.model.Post;
import com.fatmakahveci.blog.model.PostStatus;

@SpringBootTest
class PostRepositoryITests {

    @Autowired
    private PostRepository postRepository;

    @Test
    void searchesPublishedPostsCaseInsensitivelyInRealSqlite() {
        postRepository.saveAndFlush(post("SQLite Repository Guide", "sqlite-repository-guide", PostStatus.PUBLISHED));
        postRepository.saveAndFlush(post("Hidden SQLite Draft", "hidden-sqlite-draft", PostStatus.DRAFT));

        var result = postRepository.findByStatusAndTitleContainingIgnoreCase(
                PostStatus.PUBLISHED, "sqlite", PageRequest.of(0, 10));

        assertThat(result.getContent())
                .extracting(Post::getSlug)
                .contains("sqlite-repository-guide")
                .doesNotContain("hidden-sqlite-draft");
    }

    @Test
    void rejectsDuplicatePostTitles() {
        postRepository.saveAndFlush(post("Unique database title", "first-unique-slug", PostStatus.DRAFT));

        assertThatThrownBy(() -> postRepository.saveAndFlush(
                post("Unique database title", "second-unique-slug", PostStatus.DRAFT)))
                .isInstanceOf(DataAccessException.class);
    }

    @Test
    void rejectsDuplicatePostSlugs() {
        postRepository.saveAndFlush(post("First unique title", "duplicate-database-slug", PostStatus.DRAFT));

        assertThatThrownBy(() -> postRepository.saveAndFlush(
                post("Second unique title", "duplicate-database-slug", PostStatus.DRAFT)))
                .isInstanceOf(DataAccessException.class);
    }

    private Post post(String title, String slug, PostStatus status) {
        Post post = new Post(null, title, "Repository integration test content", Set.of());
        post.setSlug(slug);
        post.setStatus(status);
        return post;
    }
}
