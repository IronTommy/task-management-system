package com.example.taskmanagement.service;

import com.example.taskmanagement.dto.RegistrationDto;
import com.example.taskmanagement.dto.UserDTO;
import com.example.taskmanagement.entity.User;
import com.example.taskmanagement.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AccountService accountService;

    @Transactional
    public User createNewUser(RegistrationDto registrationDto) {
        log.debug("Method createNewUser({}) started with param: \"{}\"", RegistrationDto.class, registrationDto);
        return userRepository.findById(accountService.createNewAccount(registrationDto).getId()).orElse(null);
    }

    public User findByEmail(String email) {
        log.debug("Method findByEmail({}) started with param: \"{}\"", String.class, email);
        return userRepository.findByEmail(email).orElse(null);
    }

    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(user -> new UserDTO(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail()))
                .collect(Collectors.toList());
    }
}
