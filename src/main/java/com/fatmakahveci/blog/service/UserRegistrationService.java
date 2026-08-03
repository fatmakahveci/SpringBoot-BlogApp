package com.fatmakahveci.blog.service;

import java.util.Locale;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatmakahveci.blog.dao.UserRepository;
import com.fatmakahveci.blog.exception.DuplicateUsernameException;
import com.fatmakahveci.blog.model.BlogUser;
import com.fatmakahveci.blog.model.UserRole;

@Service
public class UserRegistrationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserRegistrationService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public BlogUser register(String username, String password) {
        String normalizedUsername = username.trim().toLowerCase(Locale.ROOT);
        if (userRepository.existsByUsernameIgnoreCase(normalizedUsername)) {
            throw new DuplicateUsernameException(normalizedUsername);
        }

        return userRepository.save(new BlogUser(
                normalizedUsername,
                passwordEncoder.encode(password),
                UserRole.AUTHOR));
    }
}
