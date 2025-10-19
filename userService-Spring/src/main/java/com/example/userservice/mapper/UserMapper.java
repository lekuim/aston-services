package com.example.userservice.mapper;

import com.example.userservice.dto.UserDto;
import com.example.userservice.entity.User;

public final class UserMapper {
    public static UserDto toDto(User u) {
        if (u == null) return null;
        UserDto d = new UserDto();
        d.setId(u.getId());
        d.setName(u.getName());
        d.setEmail(u.getEmail());
        d.setAge(u.getAge());
        d.setCreatedAt(u.getCreatedAt());
        return d;
    }

    public static User toEntity(UserDto d) {
        if (d == null) return null;
        User u = new User();
        u.setName(d.getName());
        u.setEmail(d.getEmail());
        u.setAge(d.getAge());
        return u;
    }

    public static void updateEntityFromDto(UserDto d, User u) {
        if (d.getName() != null) u.setName(d.getName());
        if (d.getEmail() != null) u.setEmail(d.getEmail());
        if (d.getAge() != null) u.setAge(d.getAge());
    }
}
