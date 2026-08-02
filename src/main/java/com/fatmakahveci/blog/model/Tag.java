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
        // Hibernate expects a mutable collection; copy input to avoid shared mutable state.
        this.posts = posts == null ? new HashSet<>() : new HashSet<>(posts);
    }

    public void deleteTagFromPosts() {
        // Update the owning side of the bidirectional relationship in memory.
        for (Post post : posts) {
            post.getTags().remove(this);
        }
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
