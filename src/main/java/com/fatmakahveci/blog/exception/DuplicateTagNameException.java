package com.fatmakahveci.blog.exception;

public class DuplicateTagNameException extends RuntimeException {

    public DuplicateTagNameException(String name) {
        super("A tag named '" + name + "' already exists.");
    }
}
