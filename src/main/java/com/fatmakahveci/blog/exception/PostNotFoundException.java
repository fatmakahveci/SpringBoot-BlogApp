package com.fatmakahveci.blog.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PostNotFoundException extends RuntimeException {
    public PostNotFoundException(Integer id) {
        super("Could not find post " + id);
    }

    public PostNotFoundException(String slug) {
        super("Could not find post " + slug);
    }
}
