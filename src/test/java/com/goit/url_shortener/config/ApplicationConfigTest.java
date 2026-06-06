package com.goit.url_shortener.config;

import com.goit.url_shortener.entity.User;
import com.goit.url_shortener.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationConfigTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ApplicationConfig applicationConfig;

    @Test
    void shouldReturnUserDetailsServiceThatFindsUser() {
        User user = new User();
        user.setUsername("tester");
        user.setPassword("password");

        when(userRepository.findByUsername("tester")).thenReturn(Optional.of(user));

        UserDetailsService userDetailsService = applicationConfig.userDetailsService();
        UserDetails result = userDetailsService.loadUserByUsername("tester");

        assertNotNull(result);
        assertEquals("tester", result.getUsername());
        verify(userRepository, times(1)).findByUsername("tester");
    }

    @Test
    void shouldReturnUserDetailsServiceThatThrowsExceptionWhenUserNotFound() {
        when(userRepository.findByUsername("wrongUser")).thenReturn(Optional.empty());

        UserDetailsService userDetailsService = applicationConfig.userDetailsService();

        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("wrongUser");
        });

        verify(userRepository, times(1)).findByUsername("wrongUser");
    }

    @Test
    void shouldReturnAuthenticationProvider() {
        AuthenticationProvider provider = applicationConfig.authenticationProvider();

        assertNotNull(provider);
        assertTrue(provider instanceof DaoAuthenticationProvider);
    }

    @Test
    void shouldReturnAuthenticationManager() throws Exception {
        AuthenticationConfiguration config = mock(AuthenticationConfiguration.class);
        AuthenticationManager mockManager = mock(AuthenticationManager.class);

        when(config.getAuthenticationManager()).thenReturn(mockManager);

        AuthenticationManager manager = applicationConfig.authenticationManager(config);

        assertNotNull(manager);
        assertEquals(mockManager, manager);
    }

    @Test
    void shouldReturnPasswordEncoder() {
        PasswordEncoder encoder = applicationConfig.passwordEncoder();

        assertNotNull(encoder);
        assertTrue(encoder instanceof BCryptPasswordEncoder);
    }
}