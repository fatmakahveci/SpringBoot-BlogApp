package com.fatmakahveci.blog.controller;

import org.springframework.security.core.Authentication;

final class PostVisibility {

    private PostVisibility() {
    }

    static boolean canViewDrafts(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_AUTHOR")
                        || authority.getAuthority().equals("ROLE_ADMIN"));
    }
}
