package com.deployfast.taskmanager.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private final String secretKey = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", secretKey);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 86400000L);
    }

    @Test
    void shouldGenerateToken() {
        UserDetails userDetails = new User("test@example.com", "password", Collections.emptyList());
        String token = jwtService.generateToken(userDetails);
        
        assertNotNull(token);
        assertEquals("test@example.com", jwtService.extractUsername(token));
    }

    @Test
    void shouldValidateToken() {
        UserDetails userDetails = new User("test@example.com", "password", Collections.emptyList());
        String token = jwtService.generateToken(userDetails);
        
        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void shouldFailForDifferentUser() {
        UserDetails user1 = new User("user1@example.com", "password", Collections.emptyList());
        UserDetails user2 = new User("user2@example.com", "password", Collections.emptyList());
        
        String token = jwtService.generateToken(user1);
        
        assertFalse(jwtService.isTokenValid(token, user2));
    }

    @Test
    void shouldReturnExpirationTime() {
        assertEquals(86400000L, jwtService.getExpirationTime());
    }
}
