package com.codetlan.shortener.service;

import com.codetlan.shortener.entity.User;
import com.codetlan.shortener.entity.VerificationToken;
import com.codetlan.shortener.exception.EmailAlreadyExistsException;
import com.codetlan.shortener.exception.InvalidTokenException;
import com.codetlan.shortener.repository.UserRepository;
import com.codetlan.shortener.repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final VerificationTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Transactional
    public void register(String email, String rawPassword) {
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException("Ya existe una cuenta con ese correo");
        }

        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(rawPassword))
                .enabled(false)
                .build();
        userRepository.save(user);

        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = VerificationToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusHours(VerificationToken.EXPIRATION_HOURS))
                .build();
        tokenRepository.save(verificationToken);

        emailService.sendConfirmationEmail(user.getEmail(), token);
    }

    @Transactional
    public void confirm(String token) {
        VerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Token inválido"));

        if (verificationToken.isExpired()) {
            throw new InvalidTokenException("El token ha expirado, solicita uno nuevo");
        }

        User user = verificationToken.getUser();
        user.setEnabled(true);
        userRepository.save(user);
        tokenRepository.delete(verificationToken);
    }
}
