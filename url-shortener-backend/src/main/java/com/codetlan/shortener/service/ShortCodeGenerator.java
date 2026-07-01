package com.codetlan.shortener.service;


import com.codetlan.shortener.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;


@Component
@RequiredArgsConstructor
public class ShortCodeGenerator {
    private static final String ALPHABET =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int CODE_LENGTH = 7;
    private static final int MAX_ATTEMPTS = 5;

    private final SecureRandom random = new SecureRandom();

    private final UrlRepository urlRepository;
    private final AnonymousUrlStore anonymousUrlStore;


    public String generateShortCode() {
        for (int i = 0; i < MAX_ATTEMPTS; i++) {
            String shortCode = randomCode(); // <- llama al generador de strings, no a sí mismo
            if (!urlRepository.existsByShortCode(shortCode) && !anonymousUrlStore.exists(shortCode)) {
                return shortCode;
            }
        }
        throw new IllegalStateException("Failed to generate shortcode for all attempts");
    }

    private String randomCode() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }
}

