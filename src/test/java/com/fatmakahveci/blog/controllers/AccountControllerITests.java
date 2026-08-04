package com.fatmakahveci.blog.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fatmakahveci.blog.dao.UserRepository;
import com.fatmakahveci.blog.model.UserRole;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Transactional
class AccountControllerITests {

    private static final String VALID_PASSWORD = "SecurePassword1!";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${BLOG_ADMIN_PASSWORD}")
    private String administratorPassword;

    @Test
    void rendersRegistrationForm() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("registration"));
    }

    @Test
    void homeShowsLoginActionsToAnonymousUsers() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("href=\"/login\"")))
                .andExpect(content().string(containsString(">Log in</a>")))
                .andExpect(content().string(containsString("href=\"/register\"")))
                .andExpect(content().string(not(containsString("action=\"/logout\""))));
    }

    @Test
    void homeShowsLogoutActionToAuthenticatedUsers() throws Exception {
        mockMvc.perform(get("/").with(user("signed-in-author").roles("AUTHOR")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("action=\"/logout\"")))
                .andExpect(content().string(containsString(">Log out</button>")))
                .andExpect(content().string(not(containsString("href=\"/login\""))))
                .andExpect(content().string(not(containsString("href=\"/register\""))));
    }

    @Test
    void logoutRedirectsToHome() throws Exception {
        mockMvc.perform(post("/logout")
                        .with(csrf())
                        .with(user("signed-in-author").roles("AUTHOR")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void registersAuthorWithEncodedPassword() throws Exception {
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("username", "New.Author")
                        .param("password", VALID_PASSWORD)
                        .param("confirmPassword", VALID_PASSWORD))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?registered"));

        var user = userRepository.findByUsernameIgnoreCase("new.author").orElseThrow();
        assertThat(user.getUsername()).isEqualTo("new.author");
        assertThat(user.getRole()).isEqualTo(UserRole.AUTHOR);
        assertThat(user.getPasswordHash()).isNotEqualTo(VALID_PASSWORD);
        assertThat(passwordEncoder.matches(VALID_PASSWORD, user.getPasswordHash())).isTrue();
    }

    @Test
    void rejectsMismatchedPasswords() throws Exception {
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("username", "mismatch")
                        .param("password", VALID_PASSWORD)
                        .param("confirmPassword", "DifferentPassword2!"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeHasFieldErrors("registration", "confirmPassword"));

        assertThat(userRepository.existsByUsernameIgnoreCase("mismatch")).isFalse();
    }

    @Test
    void rejectsDuplicateUsernameIgnoringCase() throws Exception {
        register("duplicate", VALID_PASSWORD);

        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("username", "DUPLICATE")
                        .param("password", "AnotherSecure2!")
                        .param("confirmPassword", "AnotherSecure2!"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeHasFieldErrors("registration", "username"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("already registered")));
    }

    @Test
    void registeredUserCanLogIn() throws Exception {
        register("login-user", VALID_PASSWORD);

        mockMvc.perform(post("/login")
                        .with(csrf())
                        .param("username", "login-user")
                        .param("password", VALID_PASSWORD))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void administratorLoginRequiresMfaBeforeAccessingTheApplication() throws Exception {
        mockMvc.perform(post("/login")
                        .with(csrf())
                        .param("username", "admin")
                        .param("password", administratorPassword))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/mfa"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Aa1!aaa",
            "securepassword1!",
            "SECUREPASSWORD1!",
            "SecurePassword!",
            "SecurePassword1",
            "Secure Password1!"
    })
    void rejectsPasswordsThatDoNotMeetComplexityRules(String password) throws Exception {
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("username", "weak-password-user")
                        .param("password", password)
                        .param("confirmPassword", password))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeHasFieldErrors("registration", "password"));

        assertThat(userRepository.existsByUsernameIgnoreCase("weak-password-user")).isFalse();
    }

    private void register(String username, String password) throws Exception {
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("username", username)
                        .param("password", password)
                        .param("confirmPassword", password))
                .andExpect(status().is3xxRedirection());
    }
}
