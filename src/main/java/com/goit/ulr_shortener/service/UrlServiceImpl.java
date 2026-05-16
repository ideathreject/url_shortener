package com.goit.ulr_shortener.service;

import com.goit.ulr_shortener.dto.UrlRequest;
import com.goit.ulr_shortener.dto.UrlResponse;
import com.goit.ulr_shortener.dto.UrlUpdateRequest;
import com.goit.ulr_shortener.entity.Url;
import com.goit.ulr_shortener.entity.User;
import com.goit.ulr_shortener.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {

    private final UrlRepository urlRepository;
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    @Value("${app.base-url}")
    private String baseUrl;
    private final SecureRandom random = new SecureRandom();

    @Override
    public String generateShortCode() {

        int length = 6 + random.nextInt(3);
        StringBuilder code = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            code.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        }

        return code.toString();
    }

    @Transactional
    @Override
    public String shortenUrl(UrlRequest request, User user) {
        int maxTry = 100;

        for (int i = 1; i <= maxTry; i++) {
            String shortCode = generateShortCode();
            Url url = new Url();
            url.setLongUrl(request.getOriginalUrl());
            url.setShortCode(shortCode);
            url.setUser(user);

            if (request.getExpireAt() != null) {
                url.setExpiresAt(request.getExpireAt());
            } else {
                url.setExpiresAt(LocalDateTime.now().plusDays(30));
            }
            try {
                urlRepository.saveAndFlush(url);
                return buildFullUrl(shortCode);

            } catch (DataIntegrityViolationException e) {
                if (i == maxTry) {
                    throw new IllegalStateException("Cannot create unique code after " + maxTry + " tries");
                }
            }
        }
        return null;
    }

    @Override
    public Optional<Url> getOriginalUrl(String shortCode) {
        return urlRepository.findByShortCode(shortCode)
                .filter(url -> url.getExpiresAt() == null || url.getExpiresAt().isAfter(LocalDateTime.now()))
                .map(url -> {
                    url.setClickCount(url.getClickCount() + 1);
                    urlRepository.save(url);
                    return url;
                });
    }


    @Override
    public List<UrlResponse> getUserUrls(User user) {
        return urlRepository.findAllByUser(user).stream()
                .map(url -> UrlResponse.builder()
                        .shortUrl(buildFullUrl(url.getShortCode()))
                        .originalUrl(url.getLongUrl())
                        .createdAt(url.getCreatedAt())
                        .expiresAt(url.getExpiresAt())
                        .clickCount(url.getClickCount())
                        .build())
                .toList();
    }

    @Override
    public void deleteUrl(String shortCode, User user) {
        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot find URL"));

        if (!url.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You dont have access to delete this URL");
        }

        urlRepository.delete(url);
    }

    @Override
    public List<UrlResponse> getActiveUserUrls(User user) {
        return urlRepository.findAllByUser(user).stream()
                .filter(url -> url.getExpiresAt() == null || url.getExpiresAt().isAfter(LocalDateTime.now()))
                .map(url -> UrlResponse.builder()
                        .shortUrl(buildFullUrl(url.getShortCode()))
                        .originalUrl(url.getLongUrl())
                        .createdAt(url.getCreatedAt())
                        .expiresAt(url.getExpiresAt())
                        .clickCount(url.getClickCount())
                        .build())
                .toList();
    }

    @Override
    public UrlResponse updateUrl(String shortCode, UrlUpdateRequest request, User user) {
        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new IllegalArgumentException("cannot find short code"));

        if (!url.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("you dont have access to update this URL");
        }

        if (request.getOriginalUrl() != null && !request.getOriginalUrl().isBlank()) {
            url.setLongUrl(request.getOriginalUrl());
        }

        urlRepository.save(url);

        return UrlResponse.builder()
                .shortUrl(buildFullUrl(shortCode))
                .originalUrl(url.getLongUrl())
                .createdAt(url.getCreatedAt())
                .expiresAt(url.getExpiresAt())
                .clickCount(url.getClickCount())
                .build();
    }

    private String buildFullUrl(String shortCode) {
        if (baseUrl.endsWith("/")) {
            return baseUrl + shortCode;
        }
        return baseUrl + "/" + shortCode;
    }

}