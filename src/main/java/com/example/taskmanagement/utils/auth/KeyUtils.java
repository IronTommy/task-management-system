package com.example.taskmanagement.utils.auth;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.*;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Slf4j
@Component
public class KeyUtils {

    @Value("${access-token.private}")
    private String accessTokenPrivateKeyPath;

    @Value("${access-token.public}")
    private String accessTokenPublicKeyPath;

    @Value("${refresh-token.private}")
    private String refreshTokenPrivateKeyPath;

    @Value("${refresh-token.public}")
    private String refreshTokenPublicKeyPath;

    private KeyPair accessTokenKeyPair;
    private KeyPair refreshTokenKeyPair;

    private KeyPair getKeyPair(String publicKeyPath, String privateKeyPath) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] publicKeyBytes = Files.readAllBytes(new File(publicKeyPath).toPath());
            byte[] privateKeyBytes = Files.readAllBytes(new File(privateKeyPath).toPath());

            PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));
            PrivateKey privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));

            return new KeyPair(publicKey, privateKey);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    public RSAPublicKey getAccessTokenPublicKey() {
        if (accessTokenKeyPair == null) {
            accessTokenKeyPair = getKeyPair(accessTokenPublicKeyPath, accessTokenPrivateKeyPath);
        }
        return (RSAPublicKey) accessTokenKeyPair.getPublic();
    }

    public RSAPrivateKey getAccessTokenPrivateKey() {
        if (accessTokenKeyPair == null) {
            accessTokenKeyPair = getKeyPair(accessTokenPublicKeyPath, accessTokenPrivateKeyPath);
        }
        return (RSAPrivateKey) accessTokenKeyPair.getPrivate();
    }

    public RSAPublicKey getRefreshTokenPublicKey() {
        if (refreshTokenKeyPair == null) {
            refreshTokenKeyPair = getKeyPair(refreshTokenPublicKeyPath, refreshTokenPrivateKeyPath);
        }
        return (RSAPublicKey) refreshTokenKeyPair.getPublic();
    }

    public RSAPrivateKey getRefreshTokenPrivateKey() {
        if (refreshTokenKeyPair == null) {
            refreshTokenKeyPair = getKeyPair(refreshTokenPublicKeyPath, refreshTokenPrivateKeyPath);
        }
        return (RSAPrivateKey) refreshTokenKeyPair.getPrivate();
    }
}
