package com.fatmakahveci.blog.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI blogOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Spring Boot Blog API")
                .description("Read-only API for published blog posts and tags.")
                .version("v1")
                .contact(new Contact().name("Fatma Kahveci")
                        .url("https://github.com/fatmakahveci/SpringBoot-BlogApp"))
                .license(new License().name("Apache License 2.0")
                        .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }

    @Bean
    GroupedOpenApi publicBlogApi() {
        return GroupedOpenApi.builder()
                .group("blog-api")
                .pathsToMatch("/api/**")
                .build();
    }
}
