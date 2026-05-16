package com.goit.ulr_shortener.service;

import com.goit.ulr_shortener.dto.UrlRequest;
import com.goit.ulr_shortener.dto.UrlResponse;
import com.goit.ulr_shortener.dto.UrlUpdateRequest;
import com.goit.ulr_shortener.entity.Url;
import com.goit.ulr_shortener.entity.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface UrlService {
    String generateShortCode();

    @Transactional
    String shortenUrl(UrlRequest request, User user);

    Optional<Url> getOriginalUrl(String shortCode);

    List<UrlResponse> getUserUrls(User user);

    void deleteUrl(String shortCode, User user);

    List<UrlResponse> getActiveUserUrls(User user);

    UrlResponse updateUrl(String shortCode, UrlUpdateRequest request, User user);
}
