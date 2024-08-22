package com.example.taskmanagement.security;

import com.example.taskmanagement.utils.auth.JwtTokenUtils;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtTokenUtils jwtTokenUtils;
    private final JwtAuthenticationProvider jwtAuthenticationProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.debug("Method doFilterInternal() started");

        if (request.getRequestURI().equals("/api/v1/auth/login") || request.getRequestURI().equals("/api/v1/auth/register")) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = extractAccessTokenFromCookie(request);
        log.debug("JWT token extracted from cookie: {}", jwt);

        try {
            if (jwt != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                if (jwtTokenUtils.validateToken(jwt)) {
                    BearerTokenAuthenticationToken bearerTokenAuthenticationToken = new BearerTokenAuthenticationToken(jwt);
                    Authentication authentication = jwtAuthenticationProvider.authenticate(bearerTokenAuthenticationToken);

                    if (authentication.isAuthenticated()) {
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        log.debug("Authentication successful for token: {}", jwt);
                    } else {
                        log.error("Authentication failed for token: {}", jwt);
                    }
                } else {
                    log.error("Token validation failed for token: {}", jwt);
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }
            } else {
                log.debug("No JWT found or authentication already exists.");
            }
        } catch (ExpiredJwtException e) {
            log.warn("Token has expired, passing to client for refresh. Exception: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String extractAccessTokenFromCookie(HttpServletRequest request) {
        log.debug("Method extractAccessTokenFromCookie() started");

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            log.debug("Cookies found in the request:");

            for (Cookie cookie : cookies) {
                log.debug("Cookie name: {}, Cookie value: {}", cookie.getName(), cookie.getValue());

                if ("accessToken".equals(cookie.getName())) {
                    log.debug("JWT token found in cookie: {}", cookie.getValue());
                    return cookie.getValue();
                }
            }
        }
        log.debug("Token not found in cookies.");
        return null;
    }
}

