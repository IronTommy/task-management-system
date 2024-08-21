package com.example.taskmanagement.service;

import com.example.taskmanagement.dto.*;
import com.example.taskmanagement.entity.User;
import com.example.taskmanagement.exception.AuthenticationError;
import com.example.taskmanagement.repository.UserRepository;
import com.example.taskmanagement.security.TokenGenerator;
import com.example.taskmanagement.utils.auth.CurrentUserExtractor;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserService userService;
    private final TokenGenerator tokenGenerator;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TokenRefreshService tokenRefreshService;

    public ResponseEntity<?> createNewUser(RegistrationDto registrationDto) {
        try {
            log.debug("Attempting to create new user with email: {}", registrationDto.getEmail());

            // Проверка, что пароли не null
            if (registrationDto.getPassword1() == null || registrationDto.getPassword2() == null) {
                return ResponseEntity.badRequest().body(
                        new AuthenticationError(HttpStatus.BAD_REQUEST.value(), "Пароли не могут быть пустыми"));
            }

            if (!registrationDto.getPassword1().equals(registrationDto.getPassword2())) {
                log.warn("Passwords do not match for user: {}", registrationDto.getEmail());
                return ResponseEntity.badRequest().body(
                        new AuthenticationError(HttpStatus.BAD_REQUEST.value(), "Пароли не совпадают"));
            }

            if (userService.findByEmail(registrationDto.getEmail()) != null) {
                log.warn("User with email {} already exists", registrationDto.getEmail());
                return ResponseEntity.badRequest().body(
                        new AuthenticationError(HttpStatus.BAD_REQUEST.value(), "Пользователь с такой почтой уже существует"));
            }

            User user = userService.createNewUser(registrationDto);
            log.debug("Creating new user: {}", registrationDto.getEmail());
            return ResponseEntity.ok(new UserDTO(user.getId(), user.getFirstName(), user.getPassword(), user.getEmail()));
        } catch (Exception e) {
            log.error("Error creating new user", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new AuthenticationError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Ошибка при создании пользователя"));
        }
    }


    public ResponseEntity<AuthenticateResponseDto> refreshToken(RefreshDto refreshDto, HttpServletResponse response) {
        log.debug("Received refresh token: {}", refreshDto.getRefreshToken());

        if (refreshDto.getRefreshToken() == null) {
            log.error("Refresh token is null");
            throw new RuntimeException("Refresh token is null");
        }


        AuthenticateResponseDto responseDto = tokenRefreshService.refreshToken(refreshDto);
        saveTokensInCookies(response, responseDto);
        if (responseDto != null) {
            return ResponseEntity.ok(responseDto);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }




    public ResponseEntity<?> logout(HttpServletResponse response) {

        String userEmail = CurrentUserExtractor.getCurrentUserFromAuthentication().getEmail();
        log.debug("Logging out user: {}", userEmail);


        SecurityContextHolder.clearContext();

        Cookie cookie = new Cookie("jwt", null);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return ResponseEntity.ok("Выход из системы успешно выполнен");
    }

    public AuthenticateResponseDto createAuthToken(AuthenticateDto authenticateDto, HttpServletResponse response) {
        log.debug("Logging in user: {}", authenticateDto.getEmail());

        User user = userService.findByEmail(authenticateDto.getEmail());
        if (user == null || !passwordEncoder.matches(authenticateDto.getPassword(), user.getPassword())) {
            throw new AuthenticationError(HttpStatus.UNAUTHORIZED.value(), "Неправильный логин или пароль");
        }

        AuthenticateResponseDto authenticateResponseDto = tokenGenerator.createToken(user);

        // Сохранение токенов в куки
        saveTokensInCookies(response, authenticateResponseDto);

        return authenticateResponseDto;
    }

    private void saveTokensInCookies(HttpServletResponse response, AuthenticateResponseDto tokens) {
        // Access Token Cookie
        Cookie accessTokenCookie = new Cookie("accessToken", tokens.getAccessToken());
        // accessTokenCookie.setHttpOnly(true);
        // accessTokenCookie.setSecure(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(15 * 60); // 15 минут
        accessTokenCookie.setAttribute("SameSite", "Lax");
        response.addCookie(accessTokenCookie);

        log.debug("Access token set in cookie: {}", tokens.getAccessToken());

        // Refresh Token Cookie
        Cookie refreshTokenCookie = new Cookie("refreshToken", tokens.getRefreshToken());
        // refreshTokenCookie.setHttpOnly(true);
        // refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(24 * 60 * 60); // 1 дней
        refreshTokenCookie.setAttribute("SameSite", "Lax");
        response.addCookie(refreshTokenCookie);

        log.debug("Refresh token set in cookie: {}", tokens.getRefreshToken());

    }


    public UserDTO getCurrentUser() {
        log.debug("Attempting to extract current user from authentication");
        try {
            UserDTO currentUser = CurrentUserExtractor.getCurrentUserFromAuthentication();
            log.debug("Successfully extracted current user: {}", currentUser);
            return new UserDTO(
                currentUser.getId(),
                currentUser.getFirstName(),
                currentUser.getPassword(),
                currentUser.getEmail()
            );
        } catch (Exception e) {
            log.error("Failed to extract current user from authentication", e);
            throw e;
        }
    }


}
