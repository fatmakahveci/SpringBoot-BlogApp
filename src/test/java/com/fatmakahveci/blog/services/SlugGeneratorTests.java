package com.fatmakahveci.blog.services;

import org.junit.jupiter.api.Test;

import com.fatmakahveci.blog.service.SlugGenerator;

import static org.assertj.core.api.Assertions.assertThat;

class SlugGeneratorTests {

    private final SlugGenerator slugGenerator = new SlugGenerator();

    @Test
    void createsUrlSafeStableSlug() {
        String first = slugGenerator.fromTitle("  Spring Boot & SQLite!  ");
        String second = slugGenerator.fromTitle("  Spring Boot & SQLite!  ");

        assertThat(first).matches("spring-boot-sqlite-[a-z0-9]+");
        assertThat(second).isEqualTo(first);
    }

    @Test
    void differentTitlesThatNormalizeTheSameStillHaveDifferentSlugs() {
        assertThat(slugGenerator.fromTitle("Spring Boot"))
                .isNotEqualTo(slugGenerator.fromTitle("Spring-Boot"));
    }

    @Test
    void nonLatinOrBlankTitleUsesPostFallback() {
        assertThat(slugGenerator.fromTitle("你好")).startsWith("post-");
    }
}
