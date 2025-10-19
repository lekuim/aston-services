package com.example.userservice.service;

import com.example.userservice.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto create(UserDto user);
    UserDto getById(Long id);
    List<UserDto> getAll();
    UserDto update(Long id, UserDto user);
    void delete(Long id);
}
