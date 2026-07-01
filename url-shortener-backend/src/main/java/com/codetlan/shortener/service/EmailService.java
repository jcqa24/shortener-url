package com.codetlan.shortener.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value; // ¡el de Spring, no el de Lombok!
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    public void sendConfirmationEmail(String to, String token) {
        String confirmUrl = frontendUrl + "/api/auth/confirm?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Confirma tu cuenta - Acortador de URLs");
        message.setText("""
                ¡Hola!

                Gracias por registrarte. Confirma tu cuenta dando clic en el siguiente enlace:

                %s

                Este enlace expira en 24 horas. Si no creaste esta cuenta, ignora este correo.
                """.formatted(confirmUrl));

        mailSender.send(message);
    }
}
