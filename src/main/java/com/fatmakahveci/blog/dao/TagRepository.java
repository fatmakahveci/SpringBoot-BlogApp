package com.fatmakahveci.blog.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.fatmakahveci.blog.model.Tag;

public interface TagRepository extends JpaRepository<Tag, Integer> {
    Optional<Tag> findByName(String name);
}
