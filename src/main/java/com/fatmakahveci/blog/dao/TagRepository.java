package com.fatmakahveci.blog.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fatmakahveci.blog.model.Tag;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

@Repository
public interface TagRepository extends JpaRepository<Tag, Integer> {

    @Query(value = "SELECT p.title FROM posts p where p.id = id", nativeQuery = true)
    String findNameById(Integer id);
}