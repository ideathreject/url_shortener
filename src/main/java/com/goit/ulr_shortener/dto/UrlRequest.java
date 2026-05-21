package com.goit.ulr_shortener.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

import java.time.Instant;

@Getter
@Setter
public class UrlRequest {

    @NotBlank(message = "Link cannot be blank")
    @URL(message = "Incorrect LINK")
    private String originalUrl;

    @Future(message = " Incorrect date")
    private Instant expireAt;
}