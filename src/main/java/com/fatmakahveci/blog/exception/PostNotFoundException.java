package com.fatmakahveci.blog.exception;

public class PostNotFoundException extends RuntimeException {
    public PostNotFoundException(Integer id) {
        super("Could not find post " + id);
    }
}
