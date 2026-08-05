package com.fatmakahveci.blog.config;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import com.fatmakahveci.blog.model.Post;
import com.fatmakahveci.blog.model.PostStatus;
import com.fatmakahveci.blog.model.Tag;
import com.fatmakahveci.blog.service.PostService;
import com.fatmakahveci.blog.service.TagService;

@Configuration
@Profile("dev")
public class DevelopmentSampleDataConfig {

    private static final Set<String> LEGACY_POST_TITLES = Set.of("kjn");

    private static final Set<String> LEGACY_TOPICS = Set.of(
            "Spring Boot", "Getting Started", "Security", "Testing", "pmkl", "dfas", "British Museum");

    private static final List<SamplePost> SAMPLE_POSTS = List.of(
            new SamplePost(
                    "Getting Started with Spring Blog",
                    "A Slow Sunday Morning at Home",
                    "Coffee, an open window, and the small rituals that make a quiet morning feel special.",
                    "I left my phone in the bedroom and started the day with fresh coffee and warm toast. "
                            + "The windows were open, the street was quiet, and there was nowhere I needed to be. "
                            + "Sometimes the best plan for a Sunday is simply to slow down and notice the day.",
                    Set.of("Daily Life", "Wellbeing")),
            new SamplePost(
                    "Secure Defaults for Spring Boot Applications",
                    "My Favorite Easy Weeknight Dinner",
                    "A colorful roasted vegetable pasta that is simple enough for a busy evening.",
                    "On busy weekdays I roast whatever vegetables are in the fridge while the pasta cooks. "
                            + "A little olive oil, lemon, garlic, and parmesan bring everything together. "
                            + "It is quick, comforting, and leaves enough for lunch the next day.",
                    Set.of("Daily Life", "Food")),
            new SamplePost(
                    "Testing Spring MVC from Repository to Browser",
                    "A Weekend Walk by the Sea",
                    "Notes from a breezy afternoon spent walking, talking, and watching the waves.",
                    "We took the train without making a detailed plan and followed the path beside the water. "
                            + "The wind was strong, the cafés were busy, and the horizon seemed endless. "
                            + "By sunset we were tired, happy, and already planning another small trip.",
                    Set.of("Travel", "Wellbeing")));

    @Bean
    ApplicationRunner developmentSampleData(
            PostService postService,
            TagService tagService,
            PlatformTransactionManager transactionManager) {
        TransactionTemplate transaction = new TransactionTemplate(transactionManager);
        return arguments -> transaction.executeWithoutResult(status -> seed(postService, tagService));
    }

    void seed(PostService postService, TagService tagService) {
        LEGACY_POST_TITLES.stream()
                .map(postService::findByTitle)
                .flatMap(java.util.Optional::stream)
                .map(Post::getId)
                .forEach(postService::deleteById);

        Map<String, Tag> topics = new LinkedHashMap<>();
        SAMPLE_POSTS.stream()
                .flatMap(post -> post.topics().stream())
                .distinct()
                .forEach(name -> topics.put(name, tagService.findByName(name)
                        .orElseGet(() -> tagService.createByName(name))));

        SAMPLE_POSTS.forEach(sample -> {
            if (postService.findByTitle(sample.title()).isPresent()) {
                return;
            }
            Post post = postService.findByTitle(sample.legacyTitle()).orElseGet(Post::new);
            sample.applyTo(post, topics);
            postService.save(post);
        });

        LEGACY_TOPICS.stream()
                .map(tagService::findByName)
                .flatMap(java.util.Optional::stream)
                .forEach(tag -> tagService.deleteById(tag.getId()));
    }

    private record SamplePost(String legacyTitle, String title, String summary, String content, Set<String> topics) {
        void applyTo(Post post, Map<String, Tag> availableTopics) {
            post.setTitle(title);
            post.setSummary(summary);
            post.setContent(content);
            post.setStatus(PostStatus.PUBLISHED);
            post.setTags(Set.of());
            topics.stream().map(availableTopics::get).forEach(post::addTag);
        }
    }
}
