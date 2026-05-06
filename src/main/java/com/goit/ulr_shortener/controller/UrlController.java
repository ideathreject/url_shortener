package com.goit.ulr_shortener.controller;

import com.goit.ulr_shortener.service.UrlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@Tag(name = "URL Shortener API", description = "API for create short url and redirect")
public class UrlController {

    private final UrlService urlService;

    @PostMapping("/api/v1/urls")
    @Operation(summary = "create short URL", description = "Принимает длинный URL и возвращает короткую ссылку Receive long URL and return short URL ")
    public ResponseEntity<String> shorten(@RequestBody String longUrl) {
        String resultUrl = urlService.shortenUrl(longUrl);
        return ResponseEntity.ok(resultUrl);
    }

    @GetMapping("/{shortCode}")
    @Operation(summary = "Redirect to origin URL", description = "URL Redirect user to saved long URL")
    public ResponseEntity<?> redirectToOriginal(@PathVariable String shortCode) {
        return urlService.getOriginalUrl(shortCode)
                .map(url -> ResponseEntity.status(HttpStatus.FOUND)
                        .location(URI.create(url.getLongUrl()))
                        .build())
                .orElse(ResponseEntity.notFound().build());
    }
}