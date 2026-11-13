package com.umesh.urlshortener.repository;

import com.umesh.urlshortener.entity.Link;
import com.umesh.urlshortener.entity.LinkStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Optional;

public interface LinkRepository extends JpaRepository<Link,Long> {
    boolean existsByShortCode(String shortCode);
    long countByUser_IdAndDeletedAtIsNull(long userId);
    Optional<Link>findByIdAndUser_IdAndDeletedAtIsNull(  Long id, Long userId);
    @Query("""
            select l from Link l
            where l.user.id = :userId
              and l.deletedAt is null
              and l.status in :statuses
              and (
                    lower(l.title) like :q escape '\\'
                    or lower(l.originalUrl) like :q escape '\\'
                    or lower(l.shortCode) like :q escape '\\'
              )
            """)
    Page<Link> search(
            @Param("userId") Long userId,
            @Param("statuses") Collection<LinkStatus> statuses,
            @Param("q") String likePattern,
            Pageable pageable
    );
}
