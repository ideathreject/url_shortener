package com.goit.ulr_shortener.controller;

import com.goit.ulr_shortener.dto.UrlRequest;
import com.goit.ulr_shortener.dto.UrlResponse;
import com.goit.ulr_shortener.entity.User;
import com.goit.ulr_shortener.service.UrlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "URL Shortener API", description = "API for create short url and redirect")
public class UrlController {

    private final UrlService urlService;

    @PostMapping("/api/v1/urls")
    @Operation(summary = "Create short URL", description = "Takes a long URL and gets a short URL")
    public ResponseEntity<String> shorten(
            @Valid @RequestBody UrlRequest request,
            @AuthenticationPrincipal User user
    ) {

        String resultUrl = urlService.shortenUrl(request.getOriginalUrl(), user);
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
    @GetMapping("/api/v1/urls/my")
    @Operation(summary = "My links", description = "Returns a list of links for the current user with statistics")
    public ResponseEntity<List<UrlResponse>> getMyUrls(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(urlService.getUserUrls(user));
    }

    @DeleteMapping("/api/v1/urls/{shortCode}")
    @Operation(summary = "Delete link", description = "Remove a link (for owner only)")
    public ResponseEntity<Void> deleteUrl(
            @PathVariable String shortCode,
            @AuthenticationPrincipal User user
    ) {
        urlService.deleteUrl(shortCode, user);
        return ResponseEntity.noContent().build();
    }
}