package com.goit.ulr_shortener.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

@Getter
@Setter
public class UrlUpdateRequest {

    @URL(message = "Invalid URL")
    private String originalUrl;

}