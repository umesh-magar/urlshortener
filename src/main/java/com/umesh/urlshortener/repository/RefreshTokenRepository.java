package com.umesh.urlshortener.repository;

import com.umesh.urlshortener.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<Long, RefreshToken> {
    Optional<RefreshToken> findByTokenHash(String tokenHash);
}
