package com.fatmakahveci.blog.config;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.fatmakahveci.blog.model.Post;
import com.fatmakahveci.blog.model.PostStatus;
import com.fatmakahveci.blog.model.Tag;
import com.fatmakahveci.blog.service.PostService;
import com.fatmakahveci.blog.service.TagService;

@Configuration
@Profile("dev")
public class DevelopmentSampleDataConfig {

    private static final List<SamplePost> SAMPLE_POSTS = List.of(
            new SamplePost(
                    "Getting Started with Spring Blog",
                    "Learn how to explore articles, create an account, and publish your first post.",
                    "Spring Blog keeps the writing workflow intentionally simple. Browse the public feed, "
                            + "register as an author, create a draft, and publish it when it is ready. "
                            + "Topics make related articles easier for readers to discover.",
                    Set.of("Spring Boot", "Getting Started")),
            new SamplePost(
                    "Secure Defaults for Spring Boot Applications",
                    "A practical checklist for authentication, validation, secrets, and production health checks.",
                    "Start with deny-by-default authorization, CSRF protection, strong password validation, "
                            + "and secrets supplied through environment variables. Add structured logs, separate "
                            + "readiness and liveness probes, and automated dependency scanning before deployment.",
                    Set.of("Spring Boot", "Security")),
            new SamplePost(
                    "Testing Spring MVC from Repository to Browser",
                    "Combine focused unit tests, SQLite integration tests, MockMvc, and browser-level checks.",
                    "A stable test suite uses the smallest useful layer for each behavior. Unit tests describe "
                            + "business rules, repository tests exercise real queries, MockMvc verifies HTTP contracts, "
                            + "and Playwright protects the critical user journey across responsive layouts.",
                    Set.of("Testing", "Spring Boot")));

    @Bean
    ApplicationRunner developmentSampleData(PostService postService, TagService tagService) {
        return arguments -> {
            Map<String, Tag> topics = new LinkedHashMap<>();
            SAMPLE_POSTS.stream()
                    .flatMap(post -> post.topics().stream())
                    .distinct()
                    .forEach(name -> topics.put(name, tagService.findByName(name)
                            .orElseGet(() -> tagService.createByName(name))));

            SAMPLE_POSTS.stream()
                    .filter(sample -> postService.findByTitle(sample.title()).isEmpty())
                    .map(sample -> sample.toPost(topics))
                    .forEach(postService::save);
        };
    }

    private record SamplePost(String title, String summary, String content, Set<String> topics) {
        Post toPost(Map<String, Tag> availableTopics) {
            Post post = new Post();
            post.setTitle(title);
            post.setSummary(summary);
            post.setContent(content);
            post.setStatus(PostStatus.PUBLISHED);
            topics.stream().map(availableTopics::get).forEach(post::addTag);
            return post;
        }
    }
}
