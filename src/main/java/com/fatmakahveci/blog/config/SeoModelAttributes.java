package com.fatmakahveci.blog.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class SeoModelAttributes {

    private final String baseUrl;

    public SeoModelAttributes(@Value("${blog.seo.base-url:http://localhost:8080}") String baseUrl) {
        this.baseUrl = baseUrl.replaceAll("/+$", "");
    }

    @ModelAttribute("seoBaseUrl")
    public String seoBaseUrl() {
        return baseUrl;
    }
}
