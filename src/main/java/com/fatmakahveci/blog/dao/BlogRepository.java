package com.fatmakahveci.blog.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fatmakahveci.blog.model.Blog;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface BlogRepository extends JpaRepository<Blog, Integer> {

}