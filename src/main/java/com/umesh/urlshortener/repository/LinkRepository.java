package com.umesh.urlshortener.repository;

import com.umesh.urlshortener.entity.Link;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LinkRepository extends JpaRepository<Link,Long> {
    boolean existsByShortCode(String shortCode);
    long countByUser_IdAndDeletedAtIsNull(long userId);
    Optional<Link>findByIdAndUser_IdAndDeletedAtIsNull(  Long id, Long userId);
}
