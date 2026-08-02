package com.fatmakahveci.blog.service;

import java.text.Normalizer;
import java.util.Locale;

import org.springframework.stereotype.Component;

@Component
public class SlugGenerator {

    public String fromTitle(String title) {
        String normalizedTitle = title == null ? "" : title.trim().toLowerCase(Locale.ROOT);
        String base = Normalizer.normalize(normalizedTitle, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
        if (base.isBlank()) {
            base = "post";
        }
        if (base.length() > 200) {
            base = base.substring(0, 200).replaceFirst("-+$", "");
        }
        String suffix = Integer.toUnsignedString(normalizedTitle.hashCode(), 36);
        return base + "-" + suffix;
    }
}
