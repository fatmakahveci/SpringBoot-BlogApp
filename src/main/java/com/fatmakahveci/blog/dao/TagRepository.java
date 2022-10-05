package com.fatmakahveci.blog.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fatmakahveci.blog.model.Tag;

@Repository
public interface TagRepository extends JpaRepository<Tag, Integer> {
    @Query(value = "SELECT * FROM tags t where t.name = :name", nativeQuery = true)
    Optional<Tag> findByName(@Param("name") String name);
}