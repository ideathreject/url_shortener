package com.goit.url_shortener.exception;


import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    void shouldHandleIllegalArgumentException() {
        IllegalArgumentException ex = new IllegalArgumentException("Test error");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleArgumentException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Test error", response.getBody().getMessage());
    }


    @Test
    void shouldHandleNoSuchElementException() {
        NoSuchElementException ex = new NoSuchElementException("Not found");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleNotFound(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Not found", response.getBody().getMessage());
    }

    @Test
    void shouldHandleBadCredentialsException() {
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleBadCredentials();

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Incorrect login or password", response.getBody().getMessage());
    }

    @Test
    void shouldHandleAccessDeniedException() {
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleAccessDenied();

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Access denied", response.getBody().getMessage());
    }

    @Test
    void shouldHandleExpiredJwtException() {
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleExpiredJwt();

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("JWT expired", response.getBody().getMessage());
    }

    @Test
    void shouldHandleInvalidJwtException() {
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleInvalidJwt();

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid JWT", response.getBody().getMessage());
    }
}