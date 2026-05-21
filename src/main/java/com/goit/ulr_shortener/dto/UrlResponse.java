package com.goit.ulr_shortener.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Builder
public class UrlResponse {
    private String shortUrl;
    private String originalUrl;
    private Instant createdAt;
    private Instant expiresAt;
    private Integer clickCount;
}