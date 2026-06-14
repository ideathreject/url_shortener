package com.goit.url_shortener.controller;

import com.goit.url_shortener.dto.AuthenticationRequest;
import com.goit.url_shortener.dto.AuthenticationResponse;
import com.goit.url_shortener.dto.RegisterRequest;
import com.goit.url_shortener.service.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private AuthenticationController authenticationController;

    @Test
    void shouldRegisterUser() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("tester");
        request.setPassword("password123");

        AuthenticationResponse mockResponse = AuthenticationResponse.builder()
                .token("mock-jwt-token")
                .build();

        when(authenticationService.register(request)).thenReturn(mockResponse);

        ResponseEntity<AuthenticationResponse> response = authenticationController.register(request);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());

        verify(authenticationService, times(1)).register(request);
    }

    @Test
    void shouldAuthenticateUser() {
        AuthenticationRequest request = new AuthenticationRequest();
        request.setUsername("tester");
        request.setPassword("password123");

        AuthenticationResponse mockResponse = AuthenticationResponse.builder()
                .token("mock-jwt-token")
                .build();

        when(authenticationService.authenticate(request)).thenReturn(mockResponse);

        ResponseEntity<AuthenticationResponse> response = authenticationController.authenticate(request);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());

        verify(authenticationService, times(1)).authenticate(request);
    }
}