package com.goit.ulr_shortener.dto;

import lombok.Data;
@Data
public class AuthenticationRequest {
    private String username;
    private String password;
}