package com.fatmakahveci.blog.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.junit.jupiter.api.Test;

class ProfileConfigurationTests {

    @Test
    void secureProfilesRequireExternalDatabaseAndSecrets() throws IOException {
        Properties staging = load("application-staging.properties");
        Properties production = load("application-prod.properties");

        assertThat(staging.getProperty("blog.environment")).isEqualTo("staging");
        assertThat(staging.getProperty("sentry.environment")).contains("staging");
        assertThat(staging.getProperty("spring.datasource.url")).isEqualTo("jdbc:sqlite:${BLOG_DATABASE_PATH}");
        assertThat(staging.getProperty("blog.security.require-configured-passwords")).isEqualTo("true");
        assertThat(staging.getProperty("server.servlet.session.cookie.secure")).isEqualTo("true");

        assertThat(production.getProperty("blog.environment")).isEqualTo("production");
        assertThat(production.getProperty("sentry.environment")).contains("production");
        assertThat(production.getProperty("spring.datasource.url")).isEqualTo("jdbc:sqlite:${BLOG_DATABASE_PATH}");
        assertThat(production.getProperty("blog.security.require-configured-passwords")).isEqualTo("true");
    }

    @Test
    void developmentAndE2eProfilesRemainClearlyNonProduction() throws IOException {
        assertThat(load("application-dev.properties").getProperty("blog.environment"))
                .isEqualTo("development");
        assertThat(load("application-e2e.properties").getProperty("blog.environment"))
                .isEqualTo("e2e");
    }

    private Properties load(String resource) throws IOException {
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(resource)) {
            assertThat(input).as(resource).isNotNull();
            properties.load(input);
        }
        return properties;
    }
}
