package com.goit.url_shortener.service;

import com.goit.url_shortener.dto.AuthenticationRequest;
import com.goit.url_shortener.dto.AuthenticationResponse;
import com.goit.url_shortener.dto.RegisterRequest;

public interface AuthenticationService {
    AuthenticationResponse register(RegisterRequest request);

    AuthenticationResponse authenticate(AuthenticationRequest request);
}
