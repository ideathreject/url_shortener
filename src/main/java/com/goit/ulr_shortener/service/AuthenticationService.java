package com.goit.ulr_shortener.service;

import com.goit.ulr_shortener.dto.AuthenticationRequest;
import com.goit.ulr_shortener.dto.AuthenticationResponse;
import com.goit.ulr_shortener.dto.RegisterRequest;

public interface AuthenticationService {
    AuthenticationResponse register(RegisterRequest request);

    AuthenticationResponse authenticate(AuthenticationRequest request);
}
