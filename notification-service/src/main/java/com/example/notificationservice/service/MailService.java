package com.example.notificationservice.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private final JavaMailSender mailSender;

    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @CircuitBreaker(name = "mailCircuit", fallbackMethod = "fallbackSendMail")
    public void sendMail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    public void fallbackSendMail(String to, String subject, String text, Throwable t) {
        System.out.println("Fallback: не удалось отправить письмо на " + to
                + ". Причина: " + t.getMessage());
    }
}
