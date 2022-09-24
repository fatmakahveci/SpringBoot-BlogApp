package com.fatmakahveci.blog.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fatmakahveci.blog.model.Tag;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

@Repository
public interface TagRepository extends JpaRepository<Tag, Integer> {
    List<Tag> findByName(String name);
}