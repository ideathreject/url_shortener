package com.goit.url_shortener;

import com.goit.url_shortener.dto.CreateUrlResponse;
import com.goit.url_shortener.dto.UrlRequest;
import com.goit.url_shortener.dto.UrlResponse;
import com.goit.url_shortener.dto.UrlUpdateRequest;
import com.goit.url_shortener.entity.Url;
import com.goit.url_shortener.entity.User;
import com.goit.url_shortener.repository.UrlRepository;
import com.goit.url_shortener.service.UrlServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class UrlServiceImplTest {

    @Mock
    private UrlRepository urlRepository;

    @InjectMocks
    private UrlServiceImpl urlService;

    @Test
    void shouldShortenUrlSuccessfully() {
        ReflectionTestUtils.setField(urlService, "baseUrl", "http://localhost:8080");

        User user = new User();
        user.setId(1L);
        user.setUsername("tester");

        UrlRequest request = new UrlRequest();
        request.setOriginalUrl("https://github.com");

        when(urlRepository.saveAndFlush(any(Url.class))).thenAnswer(invocation -> invocation.getArgument(0));
        CreateUrlResponse response = urlService.shortenUrl(request, user);
        assertNotNull(response);
        assertTrue(response.getShortUrl().startsWith("http://localhost:8080/"));
        assertEquals("https://github.com", response.getOriginalUrl());
        verify(urlRepository, times(1)).saveAndFlush(any(Url.class));
    }

    @Test
    void shouldReturnOriginalUrlWhenFound() {
        Url url = new Url();
        url.setLongUrl("https://github.com");

        when(urlRepository.findByShortCode("myCode")).thenReturn(Optional.of(url));

        Optional<Url> result = urlService.getOriginalUrl("myCode");

        assertTrue(result.isPresent());
        assertEquals("https://github.com", result.get().getLongUrl());
        verify(urlRepository, times(1)).findByShortCode("myCode");
    }

    @Test
    void shouldReturnEmptyWhenUrlNotFound() {
        when(urlRepository.findByShortCode("wrongCode")).thenReturn(Optional.empty());

        Optional<Url> result = urlService.getOriginalUrl("wrongCode");
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldDeleteUrlSuccessfully() {
        User user = new User();
        user.setId(1L);
        user.setUsername("tester");

        Url url = new Url();
        url.setShortCode("myCode");
        url.setUser(user);

        when(urlRepository.findByShortCode("myCode")).thenReturn(Optional.of(url));
        urlService.deleteUrl("myCode", user);
        verify(urlRepository, times(1)).delete(url);
    }

    @Test
    void shouldReturnOnlyActiveUserUrls() {
        ReflectionTestUtils.setField(urlService, "baseUrl", "http://localhost:8080/");

        User user = new User();
        user.setId(1L);
        user.setUsername("tester");

        Url activeUrlNoExpiry = new Url();
        activeUrlNoExpiry.setShortCode("active1");
        activeUrlNoExpiry.setLongUrl("https://google.com");
        activeUrlNoExpiry.setExpiresAt(null);
        activeUrlNoExpiry.setClickCount(5);

        Url activeUrlFuture = new Url();
        activeUrlFuture.setShortCode("active2");
        activeUrlFuture.setLongUrl("https://github.com");
        activeUrlFuture.setExpiresAt(Instant.now().plus(1, java.time.temporal.ChronoUnit.DAYS));
        activeUrlFuture.setClickCount(10);

        Url expiredUrl = new Url();
        expiredUrl.setShortCode("expired3");
        expiredUrl.setLongUrl("https://youtube.com");
        expiredUrl.setExpiresAt(Instant.now().minus(1, java.time.temporal.ChronoUnit.DAYS));
        expiredUrl.setClickCount(0);

        when(urlRepository.findAllByUser(user)).thenReturn(List.of(activeUrlNoExpiry, activeUrlFuture, expiredUrl));

        List<UrlResponse> result = urlService.getActiveUserUrls(user);


        verify(urlRepository, times(1)).findAllByUser(user);

        assertNotNull(result);
        assertEquals(2, result.size());

        UrlResponse response1 = result.get(0);
        assertEquals("http://localhost:8080/active1", response1.getShortUrl());
        assertEquals("https://google.com", response1.getOriginalUrl());
        assertNull(response1.getExpiresAt());
        assertEquals(5, response1.getClickCount());
        UrlResponse response2 = result.get(1);
        assertEquals("http://localhost:8080/active2", response2.getShortUrl());
        assertEquals("https://github.com", response2.getOriginalUrl());
        assertNotNull(response2.getExpiresAt());
    }

    @Test
    void shouldUpdateUrlSuccessfully() {
        ReflectionTestUtils.setField(urlService, "baseUrl", "http://localhost:8080/");

        User tester = new User();
        tester.setId(1L);
        tester.setUsername("tester");

        Url existingUrl = new Url();
        existingUrl.setShortCode("myCode");
        existingUrl.setLongUrl("https://github.com");
        existingUrl.setUser(tester);
        existingUrl.setClickCount(10);

        UrlUpdateRequest request = new UrlUpdateRequest();
        request.setOriginalUrl("https://youtube.com");
        when(urlRepository.findByShortCode("myCode")).thenReturn(Optional.of(existingUrl));
        UrlResponse response = urlService.updateUrl("myCode", request, tester);
        assertNotNull(response);
        assertEquals("http://localhost:8080/myCode", response.getShortUrl());
        assertEquals("https://youtube.com", response.getOriginalUrl());
        assertEquals(10, response.getClickCount());
        verify(urlRepository, times(1)).save(existingUrl);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingUrlWithWrongUser() {
        User tester1 = new User();
        tester1.setId(1L);

        User tester2 = new User();
        tester2.setId(2L);

        Url existingUrl = new Url();
        existingUrl.setShortCode("myCode");
        existingUrl.setUser(tester1);

        UrlUpdateRequest request = new UrlUpdateRequest();
        request.setOriginalUrl("https://youtube.com");
        when(urlRepository.findByShortCode("myCode")).thenReturn(Optional.of(existingUrl));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> urlService.updateUrl("myCode", request, tester2));
        assertEquals("you dont have access to update this URL", exception.getMessage());
        verify(urlRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentUrl() {
        User user = new User();
        user.setId(1L);

        UrlUpdateRequest request = new UrlUpdateRequest();
        request.setOriginalUrl("https://github.com");
        when(urlRepository.findByShortCode("wrongCode")).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> urlService.updateUrl("wrongCode", request, user));
        assertEquals("cannot find short code", exception.getMessage());
        verify(urlRepository, never()).save(any());
    }

    @Test
    void shouldReturnAllUserUrls() {
        ReflectionTestUtils.setField(urlService, "baseUrl", "http://localhost:8080/");

        User user = new User();
        user.setId(1L);
        user.setUsername("tester");

        Url url1 = new Url();
        url1.setShortCode("code1");
        url1.setLongUrl("https://site1.com");
        url1.setClickCount(5);

        Url url2 = new Url();
        url2.setShortCode("code2");
        url2.setLongUrl("https://site2.com");
        url2.setClickCount(15);

        when(urlRepository.findAllByUser(user)).thenReturn(List.of(url1, url2));
        List<UrlResponse> result = urlService.getUserUrls(user);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("http://localhost:8080/code1", result.get(0).getShortUrl());
        assertEquals("https://site1.com", result.get(0).getOriginalUrl());
        assertEquals(5, result.get(0).getClickCount());
        assertEquals("http://localhost:8080/code2", result.get(1).getShortUrl());
        assertEquals("https://site2.com", result.get(1).getOriginalUrl());
        assertEquals(15, result.get(1).getClickCount());
        verify(urlRepository, times(1)).findAllByUser(user);
    }

}