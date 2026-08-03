package com.fatmakahveci.blog.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import com.fatmakahveci.blog.model.Tag;

public interface TagRepository extends JpaRepository<Tag, Integer> {
    @Override
    @EntityGraph(attributePaths = {"posts", "posts.tags"})
    Optional<Tag> findById(Integer id);

    Optional<Tag> findByName(String name);
}
