package com.umesh.urlshortener.config;

import com.umesh.urlshortener.entity.Role;
import com.umesh.urlshortener.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

public class JwtService {
    private final SecretKey key;
    private final JwtProperties props;
    public JwtService(SecretKey key, JwtProperties props) {
        this.props = props;
        byte[] secret = props.secretKey().getBytes(StandardCharsets.UTF_8);
        if (secret.length < 32) {
            throw new IllegalStateException("JWT secret key must be 32 bytes");
        }
        this.key = Keys.hmacShaKeyFor(secret);
    }
    public String generateAccessToken(User user){
        Instant now=Instant.now();
        return Jwts.builder()
                .subject(String.valueOf(user.getUserId()))
                .claim("email",user.getEmail())
                .claim("role",user.getRole().name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(Duration.ofMinutes(props.accessExpirationSecretKey()))))
                .signWith(key)
                .compact();
    }
public Optional<AuthenticatedUser>parseAccessToken(String token){
        try{
            Claims claims=Jwts.parser().
                    verifyWith(key).
                    build().
                    parseSignedClaims(token).
                    getPayload();
            String subject=claims.getSubject();
            String email=claims.get("email",String.class);
            String roleName=claims.get("role",String.class);
            if(subject==null || subject.isBlank() || email==null || email.isBlank() || roleName==null || roleName.isBlank()){
                return Optional.empty();
            }
            Long userId=Long.valueOf(subject);
            Role role=Role.valueOf(roleName);
            return Optional.of(new AuthenticatedUser(userId,email,role));
        }catch (JwtException | IllegalArgumentException ex){
            return  Optional.empty();
    }
}
public long accessTokenTtlSeconds(){
        return Duration.ofMinutes(
                props.accessExpirationSecretKey()
        ).toSeconds();
}


}
