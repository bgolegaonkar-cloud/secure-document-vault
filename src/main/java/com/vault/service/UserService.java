package com.vault.service;

import com.vault.model.User;
import com.vault.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerUser(User user) {

        // Check if username already taken
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists!");
        }

        // Check if email already taken
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists!");
        }

        // NEVER save plain text password!
        // BCrypt turns "mypassword" into "$2a$10$xK8..."
        // Even if DB is hacked, passwords are safe
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Save user to MySQL
        return userRepository.save(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found!"));
    }
}