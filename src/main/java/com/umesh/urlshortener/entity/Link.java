package com.umesh.urlshortener.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
@Entity
@Table(name="links",
        indexes = {
        @Index(name="idx_links_user_id",columnList = "user_id"),
                @Index(name="idx_Links_created_at",columnList = "created_at"),
                @Index(name = "idx_links_updated_at",columnList = "updated_at")
        }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Link {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long linkId;
    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;
    @Column(name="short_code",nullable = false,unique = true,length = 32)
    private String shortCode;
    @Column(name = "original_url",nullable = false,length = 2048)
    private String originalUrl;
    @Column(length = 150)
    private String title;
    @Column(name = "expires_at")
    private Instant expiresAt;
    @Column(name="max_clicks")
    private Long maxClicks;
    @Column(name="click_counts")
    private Long clickCount=0L;
    @Column(name = "created_at")
    private Instant createdAt;
    @Column(name = "updated_at")
    private Instant updatedAt;
    @Column(name = "deleted_at")
    private Instant deletedAt;
    @PrePersist
    protected void onCreate(){
        Instant instant=Instant.now();
        createdAt=instant;
        updatedAt=instant;
    }
    @PreUpdate
    protected void onUpdate(){
        updatedAt=Instant.now();
    }

}
