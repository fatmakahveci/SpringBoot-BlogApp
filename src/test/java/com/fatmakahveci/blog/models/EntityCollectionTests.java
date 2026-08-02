package com.fatmakahveci.blog.models;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.fatmakahveci.blog.model.Post;
import com.fatmakahveci.blog.model.Tag;

import static org.assertj.core.api.Assertions.assertThat;

class EntityCollectionTests {

    @Test
    void postCopiesSubmittedTags() {
        Tag tag = new Tag(1, "java", Set.of());
        Set<Tag> submittedTags = new HashSet<>(Set.of(tag));
        Post post = new Post();

        post.setTags(submittedTags);
        submittedTags.clear();

        assertThat(post.getTags()).containsExactly(tag);
    }

    @Test
    void tagCopiesSubmittedPosts() {
        Post post = new Post(1, "Title", "Content", Set.of());
        Set<Post> submittedPosts = new HashSet<>(Set.of(post));
        Tag tag = new Tag();

        tag.setPosts(submittedPosts);
        submittedPosts.clear();

        assertThat(tag.getPosts()).containsExactly(post);
    }

    @Test
    void nullCollectionsBecomeMutableEmptySets() {
        Post post = new Post();
        Tag tag = new Tag();

        post.setTags(null);
        tag.setPosts(null);
        post.getTags().add(tag);
        tag.getPosts().add(post);

        assertThat(post.getTags()).contains(tag);
        assertThat(tag.getPosts()).contains(post);
    }

    @Test
    void deletingTagDetachesItFromEveryPost() {
        Tag tag = new Tag(1, "java", Set.of());
        Post firstPost = new Post(1, "First", "Content", Set.of(tag));
        Post secondPost = new Post(2, "Second", "Content", Set.of(tag));
        tag.setPosts(Set.of(firstPost, secondPost));

        tag.deleteTagFromPosts();

        assertThat(firstPost.getTags()).doesNotContain(tag);
        assertThat(secondPost.getTags()).doesNotContain(tag);
    }
}
