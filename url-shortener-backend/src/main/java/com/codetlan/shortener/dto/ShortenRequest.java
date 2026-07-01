package com.codetlan.shortener.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

public record ShortenRequest (
    @NotBlank(message = "La URL no puede estar vacia")
    @URL(message = "Debe ser una url valida")
    String originalUrl
)
{}
