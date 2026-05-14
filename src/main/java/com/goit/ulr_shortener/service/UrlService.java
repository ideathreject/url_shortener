package com.goit.ulr_shortener.service;

import com.goit.ulr_shortener.dto.UrlResponse;
import com.goit.ulr_shortener.entity.Url;
import com.goit.ulr_shortener.entity.User;
import com.goit.ulr_shortener.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlRepository urlRepository;
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    @Transactional
    public String shortenUrl(String longUrl, User user) {
        Url url = new Url();
        url.setLongUrl(longUrl);

        url.setShortCode(java.util.UUID.randomUUID().toString().substring(0, 8));
        url.setUser(user);
        url.setExpiresAt(LocalDateTime.now().plusDays(30));
        url = urlRepository.save(url);

        String shortCode = encode(url.getId());

        url.setShortCode(shortCode);

        return "http://localhost:8080/" + shortCode;
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

    private String encode(long num) {
        StringBuilder str = new StringBuilder();
        while (num > 0) {
            str.insert(0, ALPHABET.charAt((int) (num % 62)));
            num /= 62;
        }
        return str.toString();
    }
    public List<UrlResponse> getUserUrls(User user) {
        return urlRepository.findAllByUser(user).stream()
                .map(url -> UrlResponse.builder()
                        .shortUrl("http://localhost:8080/" + url.getShortCode())
                        .originalUrl(url.getLongUrl())
                        .createdAt(url.getCreatedAt())
                        .expiresAt(url.getExpiresAt())
                        .clickCount(url.getClickCount())
                        .build())
                .toList();
    }

    public void deleteUrl(String shortCode, User user) {
        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new RuntimeException("Cannot find URL"));

        if (!url.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You dont have a access to delete URL");
        }

        urlRepository.delete(url);
    }
}