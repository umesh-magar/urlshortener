package com.umesh.urlshortener.services;

import com.umesh.urlshortener.config.JwtProperties;
import com.umesh.urlshortener.repository.RefreshTokenRepository;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;

public class RefreshTokenService {
    private static final SecureRandom RANDOM=new SecureRandom();
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;
    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, JwtProperties jwtProperties){
        this.refreshTokenRepository=refreshTokenRepository;
        this.jwtProperties=jwtProperties;
    }
    public record  issueToken(String rawToken , Instant expiresAt){};
    private Duration ttl(){
        if(jwtProperties.accessExpirationSecretKey()<=0){
            throw new IllegalStateException("Refresh token expiry time must be greater than zero");
        }
        return Duration.ofDays(jwtProperties.accessExpirationSecretKey());
    }
}
