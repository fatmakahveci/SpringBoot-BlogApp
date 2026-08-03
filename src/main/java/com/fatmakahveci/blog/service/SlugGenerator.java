package com.fatmakahveci.blog.service;

import java.text.Normalizer;
import java.util.Locale;

import org.springframework.stereotype.Component;

@Component
public class SlugGenerator {

    public String fromTitle(String title) {
        String normalizedTitle = title == null ? "" : title.trim().toLowerCase(Locale.ROOT);
        String decomposedTitle = Normalizer.normalize(normalizedTitle, Normalizer.Form.NFD);
        StringBuilder base = new StringBuilder(Math.min(decomposedTitle.length(), 200));

        for (int index = 0; index < decomposedTitle.length() && base.length() < 200; index++) {
            char character = decomposedTitle.charAt(index);
            if (character >= 'a' && character <= 'z' || character >= '0' && character <= '9') {
                base.append(character);
            } else if (Character.getType(character) != Character.NON_SPACING_MARK
                    && !base.isEmpty()
                    && base.charAt(base.length() - 1) != '-') {
                base.append('-');
            }
        }

        while (!base.isEmpty() && base.charAt(base.length() - 1) == '-') {
            base.deleteCharAt(base.length() - 1);
        }
        String slugBase = base.isEmpty() ? "post" : base.toString();
        String suffix = Integer.toUnsignedString(normalizedTitle.hashCode(), 36);
        return slugBase + "-" + suffix;
    }
}
