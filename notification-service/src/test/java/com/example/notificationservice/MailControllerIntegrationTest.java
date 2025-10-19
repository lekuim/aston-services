package com.example.notificationservice;

import com.example.notificationservice.dto.NotificationRequest;
import com.example.notificationservice.service.MailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MailControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MailService mailService; // мок из TestConfiguration

    @TestConfiguration
    static class TestConfig {
        @Bean
        public MailService mailService() {
            return Mockito.mock(MailService.class); // создаём мок вручную
        }
    }

    @Test
    void sendEmail_shouldSendMailSuccessfully() throws Exception {
        NotificationRequest request = new NotificationRequest();
        request.setEmail("testovoe@example.com"); // изменён email
        request.setMessage("Проверка отправки!");

        mockMvc.perform(post("/mails")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Письмо отправлено на testovoe@example.com"));

        verify(mailService, times(1)).sendMail(
                "testovoe@example.com",
                "ПРОВЕРОЧНОЕ СООБЩЕНИЕ! НЕ ТРЕБУЕТ ОТВЕТА!",
                "Проверка отправки!"
        );
    }
}
