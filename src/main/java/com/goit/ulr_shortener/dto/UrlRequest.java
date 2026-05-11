package com.goit.ulr_shortener.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;
import lombok.Data;

@Data
public class UrlRequest {

    @NotBlank(message = "Link cannot be blank")
    @URL(message = "Incorrect LINK")
    private String originalUrl;
}