package com.codetlan.shortener.dto;

import java.time.LocalDateTime;

public record UrlHistoryItem(
        Long id,
        String shortCode,
        String shortUrl,
        String originalUrl,
        LocalDateTime createdAt,
        Long clickCount
) {}

