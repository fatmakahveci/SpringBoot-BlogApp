package com.fatmakahveci.blog.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegistrationForm {

    @NotBlank(message = "Username is required.")
    @Size(min = 3, max = 50, message = "Username must contain between 3 and 50 characters.")
    @Pattern(regexp = "[A-Za-z0-9._-]+", message = "Username may only contain letters, numbers, dots, underscores, and hyphens.")
    private String username;

    @NotBlank(message = "Password is required.")
    @Size(min = 8, max = 72, message = "Password must contain between 8 and 72 characters.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9\\s])(?!.*\\s).+$",
            message = "Password must include an uppercase letter, a lowercase letter, a number, and a special character, with no spaces.")
    private String password;

    @NotBlank(message = "Please confirm your password.")
    private String confirmPassword;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
