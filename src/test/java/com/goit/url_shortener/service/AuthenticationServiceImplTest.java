package com.goit.url_shortener.service;

import com.goit.url_shortener.dto.AuthenticationRequest;
import com.goit.url_shortener.dto.AuthenticationResponse;
import com.goit.url_shortener.dto.RegisterRequest;
import com.goit.url_shortener.entity.User;
import com.goit.url_shortener.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {

    @Mock
    private UserRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @Test
    void shouldRegisterUserSuccessfully() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newUser");
        request.setPassword("password123");

        when(repository.existsByUsername("newUser")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(jwtService.generateToken(any(User.class))).thenReturn("jwtToken");

        AuthenticationResponse response = authenticationService.register(request);

        assertNotNull(response);
        assertEquals("jwtToken", response.getToken());

        verify(repository, times(1)).save(argThat(user ->
                user.getUsername().equals("newUser") &&
                        user.getPassword().equals("encodedPassword") &&
                        user.getRole().equals("ROLE_USER")
        ));
    }

    @Test
    void shouldThrowExceptionWhenUsernameAlreadyExistsDuringRegistration() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("existUser");
        request.setPassword("password123");

        when(repository.existsByUsername("existUser")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authenticationService.register(request);
        });

        assertEquals("Choose another name", exception.getMessage());

        verify(repository, never()).save(any(User.class));
    }

    @Test
    void shouldAuthenticateUserSuccessfully() {
        AuthenticationRequest request = new AuthenticationRequest();
        request.setUsername("tester");
        request.setPassword("password123");

        User user = new User();
        user.setUsername("tester");

        when(repository.findByUsername("tester")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("jwtToken");

        AuthenticationResponse response = authenticationService.authenticate(request);

        assertNotNull(response);
        assertEquals("jwtToken", response.getToken());

        verify(authenticationManager, times(1)).authenticate(
                new UsernamePasswordAuthenticationToken("tester", "password123")
        );
    }

    @Test
    void shouldThrowExceptionWhenAuthenticationFails() {
        AuthenticationRequest request = new AuthenticationRequest();
        request.setUsername("tester");
        request.setPassword("wrongPassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(BadCredentialsException.class, () -> {
            authenticationService.authenticate(request);
        });

        verify(repository, never()).findByUsername(anyString());
        verify(jwtService, never()).generateToken(any());
    }
}