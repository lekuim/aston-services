package com.example.notificationservice.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class NotificationRequest {
    private String email;
    private String message;

    public NotificationRequest() {}

    public NotificationRequest(String email, String message) {
        this.email = email;
        this.message = message;
    }

}
