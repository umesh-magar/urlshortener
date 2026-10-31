package com.umesh.urlshortener.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
@Entity
@Table(
        name = "refresh_tokens",
        indexes = {
              @Index(name="idx_refresh_user",columnList = "user_id"),
                @Index(name="idx_refresh_expiry",columnList = "expires_at")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private  Long refreshTokenId;
    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;
    @Column(name = "token_hash",nullable = false,unique = true,length = 64)
    private String tokenHash;
    @Column(name = "expires_at",nullable = false)
    private Instant expiresAt;
    @Column(name = "revoke_at")
    private Instant revokeAt;
    @Column(name = "user_agent",length = 255)
    private String userAgent;
    @Column(name = "created_at",nullable = false,updatable = false)
    private Instant createdAt;
    @PrePersist
    public void onCreate(){
        createdAt=Instant.now();
    }
    public boolean isRevoked(){
        return revokeAt!=null;
    }
    public boolean isActive(){
        return !isRevoked() || !isExpired();
    }
    public boolean isExpired(){
        return !expiresAt.isAfter(Instant.now());
    }
    public void revoke(){
        if(!isRevoked()){
            this.revokeAt=Instant.now();
        }
    }

}
