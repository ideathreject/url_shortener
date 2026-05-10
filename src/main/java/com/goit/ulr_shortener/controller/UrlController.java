package com.goit.ulr_shortener.controller;

import com.goit.ulr_shortener.entity.User;
import com.goit.ulr_shortener.service.UrlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@Tag(name = "URL Shortener API", description = "API for create short url and redirect")
public class UrlController {

    private final UrlService urlService;

    @PostMapping("/api/v1/urls")
    @Operation(summary = "Создать короткую ссылку", description = "Принимает длинный URL и возвращает короткую ссылку")
    public ResponseEntity<String> shorten(
            @RequestBody String longUrl,
            @AuthenticationPrincipal User user
    ) {

        String resultUrl = urlService.shortenUrl(longUrl, user);
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