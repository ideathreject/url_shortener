package com.goit.url_shortener.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtServiceImplTest {

    @InjectMocks
    private JwtServiceImpl jwtService;

    @Mock
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        String testSecretKey = "ZGVmYXVsdFNlY3JldEtleUZvckp3dFRva444R2VuZXJhdGlvblZlcnlTZWN1cmU=";
        ReflectionTestUtils.setField(jwtService, "secretKey", testSecretKey);
    }

    @Test
    void shouldGenerateTokenAndExtractUsername() {
        when(userDetails.getUsername()).thenReturn("vlad");

        String token = jwtService.generateToken(userDetails);

        assertNotNull(token);
        assertFalse(token.isEmpty());
        String extractedUsername = jwtService.extractUsername(token);
        assertEquals("vlad", extractedUsername);
    }


    @Test
    void shouldValidateCorrectToken() {
        when(userDetails.getUsername()).thenReturn("tester");

        String token = jwtService.generateToken(userDetails);

        boolean isValid = jwtService.isTokenValid(token, userDetails);
        assertTrue(isValid);
    }

    @Test
    void shouldInvalidateTokenForWrongUser() {
        when(userDetails.getUsername()).thenReturn("tester1");
        String token1 = jwtService.generateToken(userDetails);

        UserDetails token2 = mock(UserDetails.class);
        when(token2.getUsername()).thenReturn("tester2");

        boolean isValid = jwtService.isTokenValid(token1, token2);
        assertFalse(isValid);
    }

    @Test
    void shouldRecognizeFreshTokenAsNotExpired() {
        when(userDetails.getUsername()).thenReturn("tester");
        String token = jwtService.generateToken(userDetails);
        boolean isExpired = jwtService.isTokenExpired(token);
        assertFalse(isExpired);
    }
}