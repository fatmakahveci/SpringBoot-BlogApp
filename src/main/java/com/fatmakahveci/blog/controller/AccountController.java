package com.fatmakahveci.blog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.fatmakahveci.blog.exception.DuplicateUsernameException;
import com.fatmakahveci.blog.service.UserRegistrationService;

import jakarta.validation.Valid;

@Controller
public class AccountController {

    private final UserRegistrationService registrationService;

    public AccountController(UserRegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String registrationForm(@ModelAttribute("registration") RegistrationForm registration) {
        return "register";
    }

    @PostMapping("/register")
    public String register(
            @Valid @ModelAttribute("registration") RegistrationForm registration,
            BindingResult bindingResult) {
        if (!registration.getPassword().equals(registration.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "registration.password.mismatch", "Passwords do not match.");
        }
        if (bindingResult.hasErrors()) {
            return "register";
        }

        try {
            registrationService.register(registration.getUsername(), registration.getPassword());
        } catch (DuplicateUsernameException exception) {
            bindingResult.rejectValue("username", "registration.username.duplicate", exception.getMessage());
            return "register";
        }
        return "redirect:/login?registered";
    }
}
