package com.fatmakahveci.blog.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import org.hibernate.annotations.BatchSize;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "tags")
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Tag name is required.")
    @Size(max = 50, message = "Tag name must be 50 characters or fewer.")
    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @JsonBackReference
    @BatchSize(size = 50)
    @ManyToMany(mappedBy = "tags")
    private Set<Post> posts = new HashSet<>();

    public Tag() {
    }

    public Tag(Integer id, String name, Set<Post> posts) {
        this.id = id;
        this.name = name;
        setPosts(posts);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Post> getPosts() {
        return posts;
    }

    public void setPosts(Set<Post> posts) {
        Set<Post> replacement = posts == null ? Set.of() : new HashSet<>(posts);
        new HashSet<>(this.posts).forEach(this::removePost);
        replacement.forEach(this::addPost);
    }

    public void addPost(Post post) {
        if (post != null && posts.add(post)) {
            post.addTag(this);
        }
    }

    public void removePost(Post post) {
        if (post != null && posts.remove(post)) {
            post.removeTag(this);
        }
    }

    public void deleteTagFromPosts() {
        new HashSet<>(posts).forEach(this::removePost);
    }

    @Override
    public String toString() {
        return "Tag: " + name + "\n";
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Tag other)) {
            return false;
        }
        return Objects.equals(id, other.id) && Objects.equals(name, other.name);
    }

}
