package com.example.taskmanagement.service;

import com.example.taskmanagement.dto.AuthenticateResponseDto;
import com.example.taskmanagement.dto.RefreshDto;
import com.example.taskmanagement.dto.UserDTO;
import com.example.taskmanagement.entity.User;
import com.example.taskmanagement.repository.UserRepository;
import com.example.taskmanagement.security.TokenGenerator;
import com.example.taskmanagement.utils.auth.CurrentUserExtractor;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenRefreshService {

    private final JwtDecoder jwtDecoder;
    private final UserRepository userRepository;
    private final TokenGenerator tokenGenerator;

    public AuthenticateResponseDto refreshToken(RefreshDto refreshTokenDto) {
        log.debug("Method refreshToken started: {}", refreshTokenDto);

        Jwt jwt;
        try {
            jwt = jwtDecoder.decode(refreshTokenDto.getRefreshToken());
        } catch (Exception e) {
            log.error("Token decoding failed: {}", e.getMessage());
            throw new RuntimeException("Token decoding failed");
        }

        if (jwt == null) {
            log.error("Decoded JWT is null.");
            throw new RuntimeException("Decoded JWT is null");
        }

        if (jwt.getExpiresAt().isBefore(Instant.now())) {
            log.error("Refresh token has expired.");
            throw new RuntimeException("Refresh token has expired");
        }

        UserDTO currentUserDTO = CurrentUserExtractor.getUserFromJwt(jwt);
        UUID userId = currentUserDTO.getId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found for id: " + userId));

        AuthenticateResponseDto newToken = tokenGenerator.createToken(user);

        return newToken;
    }
}
