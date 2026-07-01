package com.codetlan.shortener.controller;


import com.codetlan.shortener.dto.ShortenRequest;
import com.codetlan.shortener.dto.ShortenResponse;
import com.codetlan.shortener.entity.User;
import com.codetlan.shortener.security.AuthenticatedUserResolver;
import com.codetlan.shortener.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.codetlan.shortener.dto.UrlHistoryItem;
import org.springframework.http.HttpStatus;
import java.util.List;


@RestController
@RequestMapping("/api/urls")
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;
    private final AuthenticatedUserResolver authenticatedUserResolver;

    @PostMapping("/shorten")
    public ResponseEntity<ShortenResponse> shorten(
            @Valid @RequestBody ShortenRequest request,
            Authentication authentication) {

        User currentUser = authenticatedUserResolver.resolve(authentication);

        ShortenResponse response = urlService.shorten(request.originalUrl(), currentUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/history")
    public ResponseEntity<List<UrlHistoryItem>> history(Authentication authentication) {
        User currentUser = authenticatedUserResolver.resolve(authentication);
        return ResponseEntity.ok(urlService.history(currentUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication authentication) {
        User currentUser = authenticatedUserResolver.resolve(authentication);
        urlService.delete(id, currentUser);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
