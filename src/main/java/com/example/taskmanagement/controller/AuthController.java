package com.example.taskmanagement.controller;

import com.example.taskmanagement.dto.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/auth")
public interface AuthController {

    @PostMapping("/register")
    ResponseEntity<?> register(@RequestBody RegistrationDto registrationDto);

    @PostMapping("/refresh")
    ResponseEntity<AuthenticateResponseDto> refreshToken(@RequestBody RefreshDto refreshDto, HttpServletResponse response);

    @PostMapping("/password/recovery/{recoveryTokenId}")
    ResponseEntity<String> recoverPassword(
        @PathVariable("recoveryTokenId") String recoveryTokenId,
        @RequestBody NewPasswordDto newPasswordDto
    );

    @PostMapping("/logout")
    ResponseEntity<?> logout(HttpServletResponse response) throws IOException;

    @PostMapping("/login")
    ResponseEntity<?> login(@RequestBody AuthenticateDto authenticateDto, HttpServletResponse response);

    @PostMapping("/change-password-link")
    ResponseEntity<String> changePasswordLink(@RequestBody PasswordChangeDto passwordChangeDto);

    @PostMapping("/change-email-link")
    ResponseEntity<String> changeEmailLink(@RequestBody ChangeEmailDto changeEmailDto);

    @DeleteMapping("/admin/revokeUserTokens/{email}")
    ResponseEntity<String> revokeUserTokens(@PathVariable String email);

    @DeleteMapping("/admin/revokeAllTokens")
    ResponseEntity<String> revokeAllTokens();

    @GetMapping("/captcha")
    ResponseEntity<CaptchaDto> getCaptcha();

    @PostMapping("/admin/getActiveUsers")
    ResponseEntity<String> getActiveUsers();
}
