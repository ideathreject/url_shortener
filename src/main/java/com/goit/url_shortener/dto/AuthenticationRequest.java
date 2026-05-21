package com.goit.url_shortener.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthenticationRequest {
    @NotBlank(message = "username cannot be blank")
    private String username;
    @NotBlank(message =  "password cannot be blank")
    private String password;
}