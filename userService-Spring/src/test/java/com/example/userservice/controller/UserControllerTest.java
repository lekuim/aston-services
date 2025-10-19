package com.example.userservice.controller;

import com.example.userservice.dto.UserDto;
import com.example.userservice.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper om;

    @MockBean
    UserService service;

    @Test
    void createUser() throws Exception {
        UserDto in = new UserDto();
        in.setName("dimochka");
        in.setEmail("dimochka@example.com");
        in.setAge(30);

        UserDto out = new UserDto();
        out.setId(1L);
        out.setName(in.getName());
        out.setEmail(in.getEmail());
        out.setAge(in.getAge());

        when(service.create(any())).thenReturn(out);

        mvc.perform(post("/users")
                        .contentType("application/json")
                        .content(om.writeValueAsString(in)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/users/1"))
                .andExpect(jsonPath("$.id").value(1));

        verify(service, times(1)).create(any());
    }

    @Test
    void getUserById() throws Exception {
        UserDto out = new UserDto();
        out.setId(1L);
        out.setName("dimochka");
        out.setEmail("dimochka@example.com");
        out.setAge(30);

        when(service.getById(1L)).thenReturn(out);

        mvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("dimochka"))
                .andExpect(jsonPath("$.email").value("dimochka@example.com"));

        verify(service, times(1)).getById(1L);
    }

    @Test
    void listUsers() throws Exception {
        UserDto user1 = new UserDto();
        user1.setId(1L);
        user1.setName("dimochka");
        user1.setEmail("dimochka@example.com");
        user1.setAge(30);

        UserDto user2 = new UserDto();
        user2.setId(2L);
        user2.setName("artem");
        user2.setEmail("aArtem@example.com");
        user2.setAge(25);

        when(service.getAll()).thenReturn(List.of(user1, user2));

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));

        verify(service, times(1)).getAll();
    }

    @Test
    void updateUser() throws Exception {
        UserDto in = new UserDto();
        in.setName("dimochka-updated");
        in.setEmail("dimochka@example.com");
        in.setAge(31);

        UserDto out = new UserDto();
        out.setId(1L);
        out.setName(in.getName());
        out.setEmail(in.getEmail());
        out.setAge(in.getAge());

        when(service.update(eq(1L), any())).thenReturn(out);

        mvc.perform(put("/users/1")
                        .contentType("application/json")
                        .content(om.writeValueAsString(in)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("dimochka-updated"))
                .andExpect(jsonPath("$.age").value(31));

        verify(service, times(1)).update(eq(1L), any());
    }

    @Test
    void deleteUser() throws Exception {
        doNothing().when(service).delete(1L);

        mvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());

        verify(service, times(1)).delete(1L);
    }
}

