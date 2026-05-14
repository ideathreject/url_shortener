package com.goit.ulr_shortener.service;

import com.goit.ulr_shortener.dto.UrlResponse;
import com.goit.ulr_shortener.entity.Url;
import com.goit.ulr_shortener.entity.User;
import com.goit.ulr_shortener.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
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
public class UrlService {

    private final UrlRepository urlRepository;
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final String URL = "http://localhost:8080/";
    private final SecureRandom random = new SecureRandom();

    private String generateShortCode() {

        int length = 6 + random.nextInt(3);
        StringBuilder code = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            code.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        }

        String result = code.toString();

        if (urlRepository.findByShortCode(result).isPresent()) {
            return generateShortCode();
        }

        return result;
    }

    @Transactional
    public String shortenUrl(String longUrl, User user) {
        String shortCode = generateShortCode();
        Url url = new Url();
        url.setLongUrl(longUrl);
        url.setShortCode(shortCode);
        url.setUser(user);
        url.setExpiresAt(LocalDateTime.now().plusDays(30));

        urlRepository.save(url);
        return URL + shortCode;
    }

    public Optional<Url> getOriginalUrl(String shortCode) {
        return urlRepository.findByShortCode(shortCode)
                .filter(url -> url.getExpiresAt() == null || url.getExpiresAt().isAfter(LocalDateTime.now()))
                .map(url -> {
                    url.setClickCount(url.getClickCount() + 1);
                    urlRepository.save(url);
                    return url;
                });
    }


    public List<UrlResponse> getUserUrls(User user) {
        return urlRepository.findAllByUser(user).stream()
                .map(url -> UrlResponse.builder()
                        .shortUrl(URL + url.getShortCode())
                        .originalUrl(url.getLongUrl())
                        .createdAt(url.getCreatedAt())
                        .expiresAt(url.getExpiresAt())
                        .clickCount(url.getClickCount())
                        .build())
                .toList();
    }

    public void deleteUrl(String shortCode, User user) {
        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot find URL"));

        if (!url.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You dont have access to delete this URL");
        }

        urlRepository.delete(url);
    }

    public List<UrlResponse> getActiveUserUrls(User user) {
        return urlRepository.findAllByUser(user).stream()
                .filter(url -> url.getExpiresAt() == null || url.getExpiresAt().isAfter(LocalDateTime.now()))
                .map(url -> UrlResponse.builder()
                        .shortUrl(URL + url.getShortCode())
                        .originalUrl(url.getLongUrl())
                        .createdAt(url.getCreatedAt())
                        .expiresAt(url.getExpiresAt())
                        .clickCount(url.getClickCount())
                        .build())
                .toList();
    }
}