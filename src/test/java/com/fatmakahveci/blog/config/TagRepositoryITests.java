package com.fatmakahveci.blog.config;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.support.TransactionTemplate;

import com.fatmakahveci.blog.dao.PostRepository;
import com.fatmakahveci.blog.dao.TagRepository;
import com.fatmakahveci.blog.model.Post;
import com.fatmakahveci.blog.model.Tag;

import static org.assertj.core.api.Assertions.assertThat;
import static com.fatmakahveci.blog.support.PostFixtures.aPublishedPost;

@SpringBootTest
class TagRepositoryITests {

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Test
    void findByIdLoadsPostsForUseAfterTheTransactionCloses() {
        Integer tagId = transactionTemplate.execute(status -> {
            Tag tag = tagRepository.save(new Tag(null, "repository-test", Set.of()));
            Post post = aPublishedPost()
                    .id(null)
                    .title("Repository test post")
                    .slug("repository-test-post")
                    .tags(tag)
                    .build();
            postRepository.save(post);
            return tag.getId();
        });

        Tag loadedTag = transactionTemplate.execute(status -> tagRepository.findById(tagId).orElseThrow());

        assertThat(loadedTag.getPosts()).hasSize(1);
        assertThat(loadedTag.getPosts().iterator().next().getTags()).hasSize(1);
    }
}
