package com.example.taskmanagement.security;

import com.example.taskmanagement.utils.auth.KeyUtils;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final JwtToUserConverter jwtToUserConverter;
    private final KeyUtils keyUtils;

    @Lazy
    @Autowired
    JwtRequestFilter jwtRequestFilter;

    @Autowired
    public SecurityConfig(JwtToUserConverter jwtToUserConverter, KeyUtils keyUtils) {
        this.jwtToUserConverter = jwtToUserConverter;
        this.keyUtils = keyUtils;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/", "/index.html", "/static/**", "/favicon.ico", "/manifest.json").permitAll()
                    .requestMatchers("/api/auth/**").permitAll()
                    .requestMatchers("/api/v1/auth/refresh").permitAll()
                    .requestMatchers("/api/v1/auth/register").permitAll()
                    .requestMatchers("/api/v1/auth/login").permitAll()
                    .requestMatchers("/api/v1/auth/captcha").permitAll()
                    .requestMatchers("/actuator/prometheus").permitAll()
                    .requestMatchers("/api/v1/auth/password/recovery/*").permitAll()
                    .requestMatchers("/api/v1/auth/admin/*").hasRole("ADMIN")
                    .requestMatchers("/actuator").permitAll()
                    .requestMatchers("/actuator/*").permitAll()
                    .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                    // .anyRequest().permitAll()
                    .anyRequest().authenticated()
            )

            .oauth2ResourceServer((oauth2) ->
                oauth2.jwt((jwt) -> jwt.jwtAuthenticationConverter(jwtToUserConverter)))

            .sessionManagement((session) ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            .addFilterBefore(jwtRequestFilter, BearerTokenAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public JwtDecoder jwtAccessTokenDecoder() {
        return NimbusJwtDecoder.withPublicKey(keyUtils.getAccessTokenPublicKey()).build();
    }

    @Bean
    public JwtEncoder jwtAccessTokenEncoder() {
        JWK jwk = new RSAKey.Builder(keyUtils.getAccessTokenPublicKey())
            .privateKey(keyUtils.getAccessTokenPrivateKey())
            .build();
        JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }

    @Bean
    public JwtAuthenticationProvider jwtAuthenticationProvider(JwtDecoder jwtDecoder) {
        JwtAuthenticationProvider provider = new JwtAuthenticationProvider(jwtDecoder);
        provider.setJwtAuthenticationConverter(jwtToUserConverter);
        return provider;
    }
}
