package com.codetlan.shortener.service;


import com.codetlan.shortener.dto.ShortenRequest;
import com.codetlan.shortener.dto.ShortenResponse;
import com.codetlan.shortener.entity.Url;
import com.codetlan.shortener.entity.User;
import com.codetlan.shortener.repository.UrlRepository;
import com.codetlan.shortener.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.codetlan.shortener.dto.UrlHistoryItem;
import com.codetlan.shortener.exception.UrlNotFoundException;
import java.util.List;
import java.time.LocalDateTime;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UrlService {

    private static final long ANONYMOUS_TTL_HOURS = 2;

    private final UrlRepository urlRepository;
    private final AnonymousUrlStore anonymousUrlStore;
    private final ShortCodeGenerator shortCodeGenerator;

    @Value("${app.base-url}")
    private String baseUrl;

    @Transactional
    public ShortenResponse shorten(String originalUrl, User currentUser) {
        String code = shortCodeGenerator.generateShortCode();

        if (currentUser != null) {
            Url url = Url.builder()
                    .shortCode(code)
                    .originalUrl(originalUrl)
                    .user(currentUser)
                    .build();
            urlRepository.save(url);
            return new ShortenResponse(code, buildShortUrl(code), originalUrl, true, null);
        }

        anonymousUrlStore.save(code, originalUrl);
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(ANONYMOUS_TTL_HOURS);
        return new ShortenResponse(code, buildShortUrl(code), originalUrl, false, expiresAt);
    }

    /** Redis primero (camino rápido / anónimos), luego MariaDB. */
    @Transactional
    public Optional<String> resolve(String code) {
        Optional<String> anonymous = anonymousUrlStore.find(code);
        if (anonymous.isPresent()) {
            return anonymous;
        }

        return urlRepository.findByShortCode(code).map(url -> {
            url.setClickCount(url.getClickCount() + 1);
            urlRepository.save(url);
            return url.getOriginalUrl();
        });
    }

    private String buildShortUrl(String code) {
        return baseUrl + "/" + code;
    }

    @Transactional(readOnly = true)
    public List<UrlHistoryItem> history(User user) {
        return urlRepository.findByUserOrderByCreatedAtDesc(user).stream()
                .map(url -> new UrlHistoryItem(
                        url.getId(),
                        url.getShortCode(),
                        buildShortUrl(url.getShortCode()),
                        url.getOriginalUrl(),
                        url.getCreatedAt(),
                        url.getClickCount()))
                .toList();
    }

    @Transactional
    public void delete(Long id, User user) {
        Url url = urlRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new UrlNotFoundException("Enlace no encontrado"));
        urlRepository.delete(url);
    }
}