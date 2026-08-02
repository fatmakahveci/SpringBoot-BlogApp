package com.fatmakahveci.blog.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

final class PostPageRequest {

    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int MAX_PAGE_SIZE = 50;

    private PostPageRequest() {
    }

    static Pageable of(int page, int size, String sortOption) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.clamp(size, 1, MAX_PAGE_SIZE);
        return PageRequest.of(safePage, safeSize, sortFor(sortOption));
    }

    static int defaultPageSize() {
        return DEFAULT_PAGE_SIZE;
    }

    static String normalizeSort(String sortOption) {
        return switch (sortOption == null ? "" : sortOption) {
            case "oldest", "titleAsc", "titleDesc" -> sortOption;
            default -> "newest";
        };
    }

    private static Sort sortFor(String sortOption) {
        return switch (normalizeSort(sortOption)) {
            case "oldest" -> Sort.by(Sort.Order.asc("createdAt"), Sort.Order.asc("id"));
            case "titleAsc" -> Sort.by(Sort.Order.asc("title"), Sort.Order.asc("id"));
            case "titleDesc" -> Sort.by(Sort.Order.desc("title"), Sort.Order.desc("id"));
            default -> Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id"));
        };
    }
}
