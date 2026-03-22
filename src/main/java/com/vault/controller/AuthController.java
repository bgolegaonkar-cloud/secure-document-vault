package com.vault.controller;

import com.vault.model.User;
import com.vault.security.JwtUtil;
import com.vault.service.AuditService;
import com.vault.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AuditService auditService;

    // ── REGISTER ─────────────────────────────────────────
    // POST /api/auth/register
    // Body: { "username": "john", "password": "pass123", "email": "john@gmail.com" }
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            User savedUser = userService.registerUser(user);
            auditService.log(savedUser.getUsername(), "REGISTER", "New user registered");
            return ResponseEntity.ok(Map.of(
                "message", "User registered successfully!",
                "username", savedUser.getUsername()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", e.getMessage()
            ));
        }
    }

    // ── LOGIN ─────────────────────────────────────────────
    // POST /api/auth/login
    // Body: { "username": "john", "password": "pass123" }
    // Returns: { "token": "eyJhbGci..." }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        try {
            // Step 1: Check username and password
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    credentials.get("username"),
                    credentials.get("password")
                )
            );

            // Step 2: If correct, generate JWT token
            String token = jwtUtil.generateToken(credentials.get("username"));

            // Step 3: Log this login action
            auditService.log(credentials.get("username"), "LOGIN", "User logged in");

            // Step 4: Send token back to React
            return ResponseEntity.ok(Map.of(
                "token", token,
                "username", credentials.get("username")
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Invalid username or password!"
            ));
        }
    }
}


