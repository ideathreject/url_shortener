package com.goit.url_shortener.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Builder
public class CreateUrlResponse {
    private String shortUrl;
    private String originalUrl;
    private Instant expiresAt;
}
