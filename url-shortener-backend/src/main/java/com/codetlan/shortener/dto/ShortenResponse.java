package com.codetlan.shortener.dto;

import java.time.LocalDateTime;


public record ShortenResponse (
        String shortCode,
        String shortUrl,
        String originalUrl,
        boolean permanent,
        LocalDateTime expiresAt
){}
