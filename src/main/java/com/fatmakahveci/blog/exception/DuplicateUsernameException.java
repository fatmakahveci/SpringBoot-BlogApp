package com.fatmakahveci.blog.exception;

public class DuplicateUsernameException extends RuntimeException {

    public DuplicateUsernameException(String username) {
        super("The username '" + username + "' is already registered.");
    }
}
