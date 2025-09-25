package com.example.trainingcrm.config;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.trainingcrm.entity.User;
import com.example.trainingcrm.repository.UserRepository;

@Configuration
public class DataInitializer {

    @Bean
    public ApplicationRunner userInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            userRepository.findByUsername("admin").orElseGet(() -> {
                User admin = User.builder()
                        .username("admin")
                        .passwordHash(passwordEncoder.encode("admin123"))
                        .role("ADMIN")
                        .email("admin@example.com")
                        .enabled(true)
                        .build();
                return userRepository.save(admin);
            });
        };
    }
}
