package com.fatmakahveci.blog.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;

import com.fatmakahveci.blog.dao.UserRepository;
import com.fatmakahveci.blog.model.BlogUser;
import com.fatmakahveci.blog.model.UserRole;

@SpringBootTest
class UserRepositoryITests {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findsUsernameWithoutCaseSensitivity() {
        userRepository.saveAndFlush(new BlogUser("RepositoryUser", "encoded-password", UserRole.AUTHOR));

        assertThat(userRepository.findByUsernameIgnoreCase("repositoryuser"))
                .get()
                .extracting(BlogUser::getUsername)
                .isEqualTo("RepositoryUser");
    }

    @Test
    void rejectsAnExactDuplicateUsername() {
        userRepository.saveAndFlush(new BlogUser("unique-repository-user", "encoded-password", UserRole.AUTHOR));

        assertThatThrownBy(() -> userRepository.saveAndFlush(
                new BlogUser("unique-repository-user", "another-password", UserRole.AUTHOR)))
                .isInstanceOf(DataAccessException.class);
    }
}
