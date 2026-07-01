package com.codetlan.shortener.controller;

import com.codetlan.shortener.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RedirectController {

    private final UrlService urlService;

    @GetMapping("/{code}")
    public ResponseEntity<Void> redirect(@PathVariable String code) {
        return urlService.resolve(code)
                .map(originalUrl -> ResponseEntity.status(HttpStatus.FOUND)
                        .header(HttpHeaders.LOCATION, originalUrl)
                        .<Void>build())
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
