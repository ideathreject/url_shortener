package com.goit.url_shortener.service;

import com.goit.url_shortener.dto.CreateUrlResponse;
import com.goit.url_shortener.dto.UrlRequest;
import com.goit.url_shortener.dto.UrlResponse;
import com.goit.url_shortener.dto.UrlUpdateRequest;
import com.goit.url_shortener.entity.Url;
import com.goit.url_shortener.entity.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface UrlService {
    String generateShortCode();

    @Transactional
    CreateUrlResponse shortenUrl(UrlRequest request, User user);

    Optional<Url> getOriginalUrl(String shortCode);

    List<UrlResponse> getUserUrls(User user);

    void deleteUrl(String shortCode, User user);

    List<UrlResponse> getActiveUserUrls(User user);

    UrlResponse updateUrl(String shortCode, UrlUpdateRequest request, User user);
}
