package com.fatmakahveci.blog.config;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.support.TransactionTemplate;

import com.fatmakahveci.blog.dao.PostRepository;
import com.fatmakahveci.blog.dao.TagRepository;
import com.fatmakahveci.blog.model.Post;
import com.fatmakahveci.blog.model.PostStatus;
import com.fatmakahveci.blog.model.Tag;

import static org.assertj.core.api.Assertions.assertThat;

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
            Post post = new Post(null, "Repository test post", "content", Set.of(tag));
            post.setSlug("repository-test-post");
            post.setStatus(PostStatus.PUBLISHED);
            postRepository.save(post);
            return tag.getId();
        });

        Tag loadedTag = transactionTemplate.execute(status -> tagRepository.findById(tagId).orElseThrow());

        assertThat(loadedTag.getPosts()).hasSize(1);
        assertThat(loadedTag.getPosts().iterator().next().getTags()).hasSize(1);
    }
}
