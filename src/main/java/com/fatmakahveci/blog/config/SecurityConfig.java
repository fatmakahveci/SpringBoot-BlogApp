package com.fatmakahveci.blog.config;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

@Configuration
public class SecurityConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityConfig.class);

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(HttpMethod.DELETE, "/posts/**", "/tags/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/posts/**", "/tags/**").hasAnyRole("AUTHOR", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/posts/add", "/posts/*").hasAnyRole("AUTHOR", "ADMIN")
                        .anyRequest().permitAll())
                .formLogin(login -> login.defaultSuccessUrl("/", true))
                .logout(logout -> logout.logoutSuccessUrl("/"))
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
    UserDetailsService userDetailsService(
            PasswordEncoder passwordEncoder,
            @Value("${BLOG_ADMIN_USERNAME:admin}") String adminUsername,
            @Value("${BLOG_ADMIN_PASSWORD:}") String configuredAdminPassword,
            @Value("${BLOG_AUTHOR_USERNAME:author}") String authorUsername,
            @Value("${BLOG_AUTHOR_PASSWORD:}") String configuredAuthorPassword) {
        String adminPassword = resolvePassword("admin", configuredAdminPassword);
        String authorPassword = resolvePassword("author", configuredAuthorPassword);

        UserDetails admin = User.withUsername(adminUsername)
                .password(passwordEncoder.encode(adminPassword))
                .roles("ADMIN", "AUTHOR")
                .build();
        UserDetails author = User.withUsername(authorUsername)
                .password(passwordEncoder.encode(authorPassword))
                .roles("AUTHOR")
                .build();
        return new InMemoryUserDetailsManager(admin, author);
    }

    private String resolvePassword(String account, String configuredPassword) {
        if (configuredPassword != null && !configuredPassword.isBlank()) {
            return configuredPassword;
        }

        String generatedPassword = UUID.randomUUID().toString();
        LOGGER.warn("Generated temporary {} password: {}", account, generatedPassword);
        return generatedPassword;
    }
}
