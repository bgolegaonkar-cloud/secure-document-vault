package com.vault.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    // Called after login — creates a token for the user
    public String generateToken(String username) {
        return Jwts.builder()
            .setSubject(username)          // who this token belongs to
            .setIssuedAt(new Date())       // when it was created
            .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // expires in 24 hours
            .signWith(Keys.hmacShaKeyFor(secret.getBytes()), SignatureAlgorithm.HS256)
            .compact();
    }

    // Read the username from inside the token
    public String extractUsername(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(secret.getBytes())
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
    }

    // Check if token is valid and not expired
    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    // Check if token expiry date has passed
    private boolean isTokenExpired(String token) {
        Date expiry = Jwts.parserBuilder()
            .setSigningKey(secret.getBytes())
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getExpiration();
        return expiry.before(new Date());
    }
}