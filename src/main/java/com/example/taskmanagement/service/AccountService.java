package com.example.taskmanagement.service;

import com.example.taskmanagement.dto.RegistrationDto;
import com.example.taskmanagement.entity.Account;
import com.example.taskmanagement.repository.AccountRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;

    @Transactional
    public Account createNewAccount(RegistrationDto registrationDto) {
        log.debug("Method createNewAccount({}) started with param: \"{}\"", RegistrationDto.class, registrationDto);
        String email = registrationDto.getEmail();
        if (accountRepository.findByEmail(email).isPresent())
            throw new EntityExistsException("Account with email: \"" + email + "\" already exists");

        Account account = new Account();
        account.setFirstName(registrationDto.getFirstName());
        account.setLastName(registrationDto.getLastName());
        account.setEmail(email);
        account.setPassword(passwordEncoder.encode(registrationDto.getPassword1()));
        account.setCreatedDate(ZonedDateTime.now());
        account.setRegDate(ZonedDateTime.now());

        accountRepository.save(account);

        return account;
    }

    public Account getByEmail(String email) {
        log.debug("Method getByEmail({}) started with param: \"{}\"", String.class, email);
        return accountRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Account with email: \"" + email + "\" not found"));
    }
}
