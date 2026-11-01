package com.umesh.urlshortener.services;

import com.umesh.urlshortener.config.JwtProperties;
import com.umesh.urlshortener.entity.RefreshToken;
import com.umesh.urlshortener.entity.User;
import com.umesh.urlshortener.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Optional;
@Service
public class RefreshTokenService {
    private static final SecureRandom RANDOM=new SecureRandom();
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;
    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, JwtProperties jwtProperties){
        this.refreshTokenRepository=refreshTokenRepository;
        this.jwtProperties=jwtProperties;
    }
    public record  IssueToken(String rawToken , Instant expiresAt){};
    private Duration ttl(){
        if(jwtProperties.accessExpirationSecretKey()<=0){
            throw new IllegalStateException("Refresh token expiry time must be greater than zero");
        }
        return Duration.ofDays(jwtProperties.accessExpirationSecretKey());
    }
    public IssueToken issue(User user, String userAgent){
        byte[] randomBytes=new byte[12];
        RANDOM.nextBytes(randomBytes);
        String rawToken= Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        Instant expiresAt=Instant.now().plus(ttl());
        RefreshToken refreshToken=new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setTokenHash(hash(rawToken));
        refreshToken.setExpiresAt(expiresAt);
        if(userAgent!=null){
            refreshToken.setUserAgent(
                    userAgent.substring(
                            0,
                            Math.min(userAgent.length(),255)
                    )
            );

        }
        refreshTokenRepository.save(refreshToken);
        return new IssueToken(rawToken,expiresAt);
    }
    public Optional<RefreshToken> findByRaw(String rawToken){
        if(rawToken==null || rawToken.isBlank()){
            return Optional.empty();
        }
        return refreshTokenRepository.findByTokenHash(hash(rawToken));
    }
    private String hash(String rawToken){
        try{
            MessageDigest digest=MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes=digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashedBytes);
        }catch (NoSuchAlgorithmException e){
            throw new IllegalStateException("SHA-256 algorithm unavailable");
        }
    }
}
