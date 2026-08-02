package com.fatmakahveci.blog.exception;

public class DuplicatePostTitleException extends RuntimeException {

    public DuplicatePostTitleException(String title) {
        super("A post titled '" + title + "' already exists.");
    }
}
