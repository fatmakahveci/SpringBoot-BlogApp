package com.fatmakahveci.blog.controller;

import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import com.fatmakahveci.blog.model.PostStatus;
import com.fatmakahveci.blog.service.PostService;
import com.fatmakahveci.blog.service.TagService;

@Controller
public class SeoController {

    private final PostService postService;
    private final TagService tagService;
    private final String baseUrl;

    public SeoController(
            PostService postService,
            TagService tagService,
            @Value("${blog.seo.base-url:http://localhost:8080}") String baseUrl) {
        this.postService = postService;
        this.tagService = tagService;
        this.baseUrl = baseUrl.replaceAll("/+$", "");
    }

    @GetMapping(value = "/robots.txt", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String robots() {
        return "User-agent: *\n"
                + "Allow: /\n"
                + "Disallow: /login\n"
                + "Disallow: /register\n"
                + "Disallow: /posts/\n"
                + "Disallow: /api/\n"
                + "Disallow: /actuator/\n"
                + "Sitemap: " + baseUrl + "/sitemap.xml\n";
    }

    @GetMapping(value = "/sitemap.xml", produces = MediaType.APPLICATION_XML_VALUE)
    @ResponseBody
    public String sitemap() {
        StringBuilder xml = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
                .append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n");
        appendUrl(xml, baseUrl + "/", null);

        postService.findPublished().forEach(post -> appendUrl(
                xml,
                baseUrl + "/p/" + post.getSlug(),
                DateTimeFormatter.ISO_INSTANT.format(post.getUpdatedAt())));

        tagService.findByPostStatus(PostStatus.PUBLISHED)
                .forEach(tag -> appendUrl(xml, baseUrl + "/tags/" + tag.getId(), null));

        return xml.append("</urlset>\n").toString();
    }

    private void appendUrl(StringBuilder xml, String location, String lastModified) {
        xml.append("  <url>\n")
                .append("    <loc>").append(HtmlUtils.htmlEscape(location)).append("</loc>\n");
        if (lastModified != null) {
            xml.append("    <lastmod>").append(lastModified).append("</lastmod>\n");
        }
        xml.append("  </url>\n");
    }
}
