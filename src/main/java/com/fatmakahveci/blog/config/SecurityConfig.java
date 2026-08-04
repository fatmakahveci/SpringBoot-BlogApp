package com.fatmakahveci.blog.config;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

import com.fatmakahveci.blog.dao.UserRepository;
import com.fatmakahveci.blog.model.BlogUser;
import com.fatmakahveci.blog.model.UserRole;

@Configuration
public class SecurityConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityConfig.class);

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            AdminMfaFilter adminMfaFilter,
            RateLimitFilter rateLimitFilter,
            MfaAuthenticationSuccessHandler successHandler) throws Exception {
        return http
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(HttpMethod.DELETE, "/posts/**", "/tags/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/tags/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/posts/**", "/tags/**").hasAnyRole("AUTHOR", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/tags/*/edit").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/posts/add", "/posts/*").hasAnyRole("AUTHOR", "ADMIN")
                        .anyRequest().permitAll())
                .formLogin(login -> login.loginPage("/login").successHandler(successHandler).permitAll())
                .logout(logout -> logout.logoutSuccessUrl("/"))
                .addFilterBefore(rateLimitFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(adminMfaFilter, UsernamePasswordAuthenticationFilter.class)
                .headers(headers -> headers
                        // Keep resources local and prevent the application from being framed.
                        .contentSecurityPolicy(csp -> csp.policyDirectives(
                                "default-src 'self'; img-src 'self' data:; style-src 'self' 'unsafe-inline'; "
                                        + "script-src 'self'; frame-ancestors 'none'; form-action 'self'"))
                        .referrerPolicy(referrer -> referrer
                                .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER))
                        .permissionsPolicyHeader(permissions -> permissions.policy("camera=(), microphone=(), geolocation=()")))
                .build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    UserDetailsService userDetailsService(UserRepository userRepository) {
        return username -> userRepository.findByUsernameIgnoreCase(username)
                .map(user -> org.springframework.security.core.userdetails.User
                        .withUsername(user.getUsername())
                        .password(user.getPasswordHash())
                        .roles(user.getRole().name())
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Bean
    ApplicationRunner configuredAccounts(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            @Value("${BLOG_ADMIN_USERNAME:admin}") String adminUsername,
            @Value("${BLOG_ADMIN_PASSWORD:}") String configuredAdminPassword,
            @Value("${BLOG_AUTHOR_USERNAME:author}") String authorUsername,
            @Value("${BLOG_AUTHOR_PASSWORD:}") String configuredAuthorPassword,
            @Value("${blog.security.require-configured-passwords:false}") boolean requireConfiguredPasswords) {
        return arguments -> {
            saveConfiguredAccount(userRepository, passwordEncoder, adminUsername,
                    resolvePassword("admin", configuredAdminPassword, requireConfiguredPasswords), UserRole.ADMIN);
            saveConfiguredAccount(userRepository, passwordEncoder, authorUsername,
                    resolvePassword("author", configuredAuthorPassword, requireConfiguredPasswords), UserRole.AUTHOR);
        };
    }

    private void saveConfiguredAccount(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            String username,
            String password,
            UserRole role) {
        BlogUser user = userRepository.findByUsernameIgnoreCase(username)
                .orElseGet(() -> new BlogUser(username.trim(), "", role));
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setRole(role);
        userRepository.save(user);
    }

    private String resolvePassword(String account, String configuredPassword, boolean requireConfiguredPassword) {
        if (configuredPassword != null && !configuredPassword.isBlank()) {
            return configuredPassword;
        }

        if (requireConfiguredPassword) {
            throw new IllegalStateException("BLOG_" + account.toUpperCase() + "_PASSWORD must be configured");
        }

        String generatedPassword = UUID.randomUUID().toString();
        LOGGER.warn("Generated temporary {} password: {}", account, generatedPassword);
        return generatedPassword;
    }
}
