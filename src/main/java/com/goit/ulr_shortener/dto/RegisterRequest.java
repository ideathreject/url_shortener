package com.goit.ulr_shortener.dto;
import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
}