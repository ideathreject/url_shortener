package com.goit.ulr_shortener.service;

import com.goit.ulr_shortener.entity.Url;
import com.goit.ulr_shortener.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlRepository urlRepository;
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    @Transactional
    public String shortenUrl(String longUrl) {
        Url url = new Url();
        url.setLongUrl(longUrl);

        url.setShortCode(java.util.UUID.randomUUID().toString().substring(0, 8));

        url = urlRepository.save(url);

        String shortCode = encode(url.getId());

        url.setShortCode(shortCode);

        return "http://localhost:8080/" + shortCode;
    }

    public Optional<Url> getOriginalUrl(String shortCode) {
        return urlRepository.findByShortCode(shortCode)
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
}