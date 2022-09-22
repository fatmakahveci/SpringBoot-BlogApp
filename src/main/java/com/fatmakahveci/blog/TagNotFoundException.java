package com.fatmakahveci.blog;

public class TagNotFoundException extends RuntimeException {
    public TagNotFoundException(Integer id) {
        super("Could not find tag " + id);
    }
}
