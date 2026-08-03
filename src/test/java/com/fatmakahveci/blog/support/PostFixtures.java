package com.fatmakahveci.blog.support;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import com.fatmakahveci.blog.model.Post;
import com.fatmakahveci.blog.model.PostStatus;
import com.fatmakahveci.blog.model.Tag;

public final class PostFixtures {
    public static final String DEFAULT_TITLE = "Fixture post";
    public static final String DEFAULT_CONTENT = "Fixture post content";
    public static final String DEFAULT_SLUG = "fixture-post";
    public static final Instant DEFAULT_CREATED_AT = Instant.parse("2026-08-03T08:00:00Z");
    public static final Instant DEFAULT_UPDATED_AT = Instant.parse("2026-08-03T09:00:00Z");

    private PostFixtures() {
    }

    public static PostBuilder aPost() {
        return new PostBuilder();
    }

    public static PostBuilder aPublishedPost() {
        return aPost().status(PostStatus.PUBLISHED);
    }

    public static PostBuilder aDraftPost() {
        return aPost().status(PostStatus.DRAFT);
    }

    public static final class PostBuilder {
        private Integer id = 1;
        private String title = DEFAULT_TITLE;
        private String content = DEFAULT_CONTENT;
        private String slug = DEFAULT_SLUG;
        private PostStatus status = PostStatus.DRAFT;
        private Set<Tag> tags = Set.of();
        private Instant createdAt = DEFAULT_CREATED_AT;
        private Instant updatedAt = DEFAULT_UPDATED_AT;

        private PostBuilder() {
        }

        public PostBuilder id(Integer id) {
            this.id = id;
            return this;
        }

        public PostBuilder title(String title) {
            this.title = title;
            return this;
        }

        public PostBuilder content(String content) {
            this.content = content;
            return this;
        }

        public PostBuilder slug(String slug) {
            this.slug = slug;
            return this;
        }

        public PostBuilder status(PostStatus status) {
            this.status = status;
            return this;
        }

        public PostBuilder tags(Tag... tags) {
            this.tags = tags == null ? Set.of() : new HashSet<>(Set.of(tags));
            return this;
        }

        public Post build() {
            Post post = new Post(id, title, content, tags);
            post.setSlug(slug);
            post.setStatus(status);
            post.setCreatedAt(createdAt);
            post.setUpdatedAt(updatedAt);
            return post;
        }
    }
}
