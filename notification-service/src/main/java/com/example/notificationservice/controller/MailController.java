package com.example.notificationservice.controller;

import com.example.notificationservice.dto.NotificationRequest;
import com.example.notificationservice.service.MailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mails")
public class MailController {

    private final MailService mailService;

    public MailController(MailService mailService) {
        this.mailService = mailService;
    }

    @PostMapping
    public ResponseEntity<String> sendEmail(@RequestBody NotificationRequest request) {
        mailService.sendMail(
                request.getEmail(),
                "ПРОВЕРОЧНОЕ СООБЩЕНИЕ! НЕ ТРЕБУЕТ ОТВЕТА!",
                request.getMessage()
        );
        return ResponseEntity.ok("Письмо отправлено на " + request.getEmail());
    }
}
