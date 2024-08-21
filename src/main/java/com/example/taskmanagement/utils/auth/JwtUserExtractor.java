package com.example.taskmanagement.utils.auth;

import com.example.taskmanagement.dto.UserDTO;
import com.example.taskmanagement.security.SecurityConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUserExtractor {

    private final SecurityConfig securityConfig;

    @Deprecated
    public UserDTO getUserFromToken(String token) {
        log.debug("Method getUserFromToken() started with token: {}", token);

        JwtDecoder jwtDecoder = securityConfig.jwtAccessTokenDecoder();
        Jwt jwt = jwtDecoder.decode(token);

        UUID id = UUID.fromString(jwt.getClaim("sub"));
        String username = jwt.getClaim("firstName");
        String email = jwt.getClaim("email");

        UserDTO userDTO = new UserDTO(id, username, null, email);

        log.debug("Method getUserFromToken() finished with result: {}", userDTO);

        return userDTO;
    }

    public UserDTO getUserFromAuthentication(Authentication authentication) {
        log.debug("Method getUserFromAuthentication() started with authentication: {}", authentication);

        if (authentication.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            UUID id = UUID.fromString(jwt.getClaim("sub"));
            String username = jwt.getClaim("firstName");
            String email = jwt.getClaim("email");

            UserDTO userDTO = new UserDTO(id, username, null, email);
            log.debug("Method getUserFromAuthentication() finished with result: {}", userDTO);

            return userDTO;
        } else {
            log.error("Authentication principal is not an instance of Jwt. Principal: {}", authentication.getPrincipal());
            throw new ClassCastException("Authentication principal is not an instance of Jwt");
        }
    }

}
