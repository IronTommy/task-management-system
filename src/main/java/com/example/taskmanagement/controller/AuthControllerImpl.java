package com.example.taskmanagement.controller;

import com.example.taskmanagement.dto.*;
import com.example.taskmanagement.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthControllerImpl implements AuthController {

    private final AuthService authService;

    @Override
    public ResponseEntity<?> register(@RequestBody RegistrationDto registrationDto) {
        return ResponseEntity.ok(authService.createNewUser(registrationDto));
    }

    @Override
    public ResponseEntity<AuthenticateResponseDto> refreshToken(@RequestBody RefreshDto refreshDto, HttpServletResponse response) {
        return authService.refreshToken(refreshDto, response);
    }

    @Override
    public ResponseEntity<String> recoverPassword(String recoveryTokenId, NewPasswordDto newPasswordDto) {
        return null;
    }


    @Override
    public ResponseEntity<?> logout(HttpServletResponse response) {
        return ResponseEntity.ok(authService.logout(response));
    }

    @Override
    public ResponseEntity<AuthenticateResponseDto> login(@RequestBody AuthenticateDto authenticateDto, HttpServletResponse response) {
        return ResponseEntity.ok(authService.createAuthToken(authenticateDto, response));
    }

    @Override
    public ResponseEntity<String> changePasswordLink(PasswordChangeDto passwordChangeDto) {
        return null;
    }

    @Override
    public ResponseEntity<String> changeEmailLink(ChangeEmailDto changeEmailDto) {
        return null;
    }

    @Override
    public ResponseEntity<String> revokeUserTokens(String email) {
        return null;
    }

    @Override
    public ResponseEntity<String> revokeAllTokens() {
        return null;
    }

    @Override
    public ResponseEntity<CaptchaDto> getCaptcha() {
        return null;
    }

    @Override
    public ResponseEntity<String> getActiveUsers() {
        return null;
    }


}
