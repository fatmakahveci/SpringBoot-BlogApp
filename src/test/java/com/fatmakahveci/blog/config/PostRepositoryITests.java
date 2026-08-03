package com.fatmakahveci.blog.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;

import com.fatmakahveci.blog.dao.PostRepository;
import com.fatmakahveci.blog.model.Post;
import com.fatmakahveci.blog.model.PostStatus;

import static com.fatmakahveci.blog.support.PostFixtures.aPost;

@SpringBootTest
class PostRepositoryITests {

    @Autowired
    private PostRepository postRepository;

    @Test
    void searchesPublishedPostsCaseInsensitivelyInRealSqlite() {
        postRepository.saveAndFlush(aPost()
                .id(null)
                .title("SQLite Repository Guide")
                .slug("sqlite-repository-guide")
                .status(PostStatus.PUBLISHED)
                .build());
        postRepository.saveAndFlush(aPost().id(null).title("Hidden SQLite Draft").slug("hidden-sqlite-draft").build());

        var result = postRepository.findByStatusAndTitleContainingIgnoreCase(
                PostStatus.PUBLISHED, "sqlite", PageRequest.of(0, 10));

        assertThat(result.getContent())
                .extracting(Post::getSlug)
                .contains("sqlite-repository-guide")
                .doesNotContain("hidden-sqlite-draft");
    }

    @Test
    void rejectsDuplicatePostTitles() {
        postRepository.saveAndFlush(aPost().id(null).title("Unique database title").slug("first-unique-slug").build());

        assertThatThrownBy(() -> postRepository.saveAndFlush(
                aPost().id(null).title("Unique database title").slug("second-unique-slug").build()))
                .isInstanceOf(DataAccessException.class);
    }

    @Test
    void rejectsDuplicatePostSlugs() {
        postRepository.saveAndFlush(aPost().id(null).title("First unique title").slug("duplicate-database-slug").build());

        assertThatThrownBy(() -> postRepository.saveAndFlush(
                aPost().id(null).title("Second unique title").slug("duplicate-database-slug").build()))
                .isInstanceOf(DataAccessException.class);
    }
}
