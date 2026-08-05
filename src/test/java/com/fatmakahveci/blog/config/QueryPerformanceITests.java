package com.fatmakahveci.blog.config;

import static com.fatmakahveci.blog.support.PostFixtures.aPublishedPost;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import com.fatmakahveci.blog.dao.PostRepository;
import com.fatmakahveci.blog.dao.TagRepository;
import com.fatmakahveci.blog.model.Post;
import com.fatmakahveci.blog.model.Tag;
import com.fatmakahveci.blog.service.PostService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;

@SpringBootTest(properties = "spring.jpa.properties.hibernate.generate_statistics=true")
class QueryPerformanceITests {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private PostService postService;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    @Transactional
    void paginatedPostsLoadTagsInOneBoundedBatchInsteadOfOneQueryPerPost() {
        Tag tag = tagRepository.saveAndFlush(new Tag(null, "performance", Set.of()));
        for (int index = 1; index <= 3; index++) {
            Post post = aPublishedPost()
                    .id(null)
                    .title("Performance post " + index)
                    .slug("performance-post-" + index)
                    .build();
            post.addTag(tag);
            postRepository.save(post);
        }
        postRepository.flush();
        entityManager.clear();

        Statistics statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        statistics.clear();

        var page = postService.findAll("", PageRequest.of(0, 10), false);

        assertThat(page.getContent()).hasSize(3);
        assertThat(page.getContent()).allMatch(post -> Hibernate.isInitialized(post.getTags()));
        assertThat(statistics.getPrepareStatementCount()).isLessThanOrEqualTo(3);
    }
}
