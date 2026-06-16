package com.goit.url_shortener.dto;

import jakarta.validation.constraints.Future;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

import java.time.Instant;

@Getter
@Setter
public class UrlUpdateRequest {

    @URL(message = "Invalid URL")
    private String originalUrl;

    @Future(message = "Date must be in the future")
    private Instant expiresAt;

}