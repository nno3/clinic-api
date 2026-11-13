package com.example.part1.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Disable security for all endpoints
        http.csrf().disable()
                .authorizeRequests()
                .anyRequest().permitAll(); // Allow all requests without authentication
        return http.build();
    }
}
