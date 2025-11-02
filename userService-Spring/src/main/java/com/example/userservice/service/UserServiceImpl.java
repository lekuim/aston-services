package com.example.userservice.service;

import com.example.userservice.dto.UserDto;
import com.example.userservice.entity.User;
import com.example.userservice.exception.NotFoundException;
import com.example.userservice.kafka.UserEventProducer;
import com.example.userservice.mapper.UserMapper;
import com.example.userservice.repository.UserRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository repo;
    private final UserEventProducer producer;

    public UserServiceImpl(UserRepository repo, UserEventProducer producer) {
        this.repo = repo;
        this.producer = producer;
    }

    @Override
    public UserDto create(UserDto userDto) {
        User u = UserMapper.toEntity(userDto);
        User saved = repo.save(u);
        sendUserEventWithCircuitBreaker("CREATE", saved.getEmail());
        return UserMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getById(Long id) {
        User u = repo.findById(id).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        return UserMapper.toDto(u);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAll() {
        return repo.findAll().stream().map(UserMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public UserDto update(Long id, UserDto userDto) {
        User u = repo.findById(id).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        UserMapper.updateEntityFromDto(userDto, u);
        User saved = repo.save(u);
        sendUserEventWithCircuitBreaker("UPDATE", saved.getEmail());
        return UserMapper.toDto(saved);
    }

    @Override
    public void delete(Long id) {
        if (!repo.existsById(id)) throw new NotFoundException("Пользователь не найден");
        String email = repo.getReferenceById(id).getEmail();
        sendUserEventWithCircuitBreaker("DELETE", email);
        repo.deleteById(id);
    }


    @CircuitBreaker(name = "userEventCircuit", fallbackMethod = "fallbackUserEvent")
    public void sendUserEventWithCircuitBreaker(String action, String email) {
        producer.sendUserEvent(action, email);
    }

    public void fallbackUserEvent(String action, String email, Throwable t) {
        System.out.println("Fallback: событие '" + action + "' для " + email + " не отправлено. Причина: " + t.getMessage());
    }
}
