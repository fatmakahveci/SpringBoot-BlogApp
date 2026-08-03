package com.fatmakahveci.blog.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fatmakahveci.blog.model.BlogUser;

public interface UserRepository extends JpaRepository<BlogUser, Integer> {
    Optional<BlogUser> findByUsernameIgnoreCase(String username);

    boolean existsByUsernameIgnoreCase(String username);
}
