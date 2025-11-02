package com.example.notificationservice.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final JavaMailSender mailSender;

    public NotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @KafkaListener(topics = "user-operations", groupId = "notification-group")
    public void listen(String message) {
        String[] parts = message.split(":");
        String operation = parts[0];
        String email = parts[1];

        sendEmailWithCircuitBreaker(operation, email);
    }

    // ---------------- Circuit Breaker ----------------
    @CircuitBreaker(name = "mailCircuit", fallbackMethod = "fallbackSendEmail")
    public void sendEmailWithCircuitBreaker(String operation, String email) {
        String subject = "Уведомление от сайта";
        String body = switch (operation) {
            case "CREATE" -> "Здравствуйте! Ваш аккаунт на сайте был успешно создан.";
            case "DELETE" -> "Здравствуйте! Ваш аккаунт был удалён.";
            default -> "Операция неизвестна";
        };

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(email);
        mail.setSubject(subject);
        mail.setText(body);
        mailSender.send(mail);
    }

    public void fallbackSendEmail(String operation, String email, Throwable t) {
        System.out.println("Fallback: не удалось отправить письмо '" + operation + "' для " + email
                + ". Причина: " + t.getMessage());
    }
}
