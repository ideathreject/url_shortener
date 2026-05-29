package com.goit.url_shortener.controller;

import com.goit.url_shortener.dto.CreateUrlResponse;
import com.goit.url_shortener.dto.UrlRequest;
import com.goit.url_shortener.dto.UrlResponse;
import com.goit.url_shortener.entity.Url;
import com.goit.url_shortener.entity.User;
import com.goit.url_shortener.service.UrlService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlControllerTest {

    @Mock
    private UrlService urlService;

    @InjectMocks
    private UrlController urlController;

    @Test
    void shouldCreateShortUrl() {
        User user = new User();
        UrlRequest request = new UrlRequest();
        request.setOriginalUrl("https://youtube.com");

        CreateUrlResponse mockResponse = CreateUrlResponse.builder()
                .shortUrl("http://youtube:8080/ggg")
                .originalUrl("https://youtube.com")
                .build();

        when(urlService.shortenUrl(request, user)).thenReturn(mockResponse);

        ResponseEntity<CreateUrlResponse> response = urlController.shorten(request, user);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
        verify(urlService, times(1)).shortenUrl(request, user);
    }

    @Test
    void shouldRedirectToOriginalUrl() {
        String shortCode = "myCode";
        Url url = new Url();
        url.setLongUrl("https://youtube.com");

        when(urlService.getOriginalUrl(shortCode)).thenReturn(Optional.of(url));

        ResponseEntity<Void> response = urlController.redirectToOriginal(shortCode);

        assertNotNull(response);
        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertEquals(URI.create("https://youtube.com"), response.getHeaders().getLocation());
    }

    @Test
    void shouldReturnNotFoundWhenRedirectingUnknownUrl() {
        when(urlService.getOriginalUrl("notExistCode")).thenReturn(Optional.empty());

        ResponseEntity<Void> response = urlController.redirectToOriginal("notExistCode");

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void shouldReturnMyUrls() {
        User user = new User();
        List<UrlResponse> mockList = List.of(UrlResponse.builder().build());

        when(urlService.getUserUrls(user)).thenReturn(mockList);

        ResponseEntity<List<UrlResponse>> response = urlController.getMyUrls(user);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockList, response.getBody());
    }

    @Test
    void shouldReturnMyActiveUrls() {
        User user = new User();
        List<UrlResponse> mockList = List.of(UrlResponse.builder().build());

        when(urlService.getActiveUserUrls(user)).thenReturn(mockList);

        ResponseEntity<List<UrlResponse>> response = urlController.getMyActiveUrls(user);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockList, response.getBody());
    }

    @Test
    void shouldDeleteUrl() {
        User user = new User();
        String shortCode = "myCode";

        ResponseEntity<Void> response = urlController.deleteUrl(shortCode, user);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        verify(urlService, times(1)).deleteUrl(shortCode, user);
    }
}