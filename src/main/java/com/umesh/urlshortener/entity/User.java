package com.umesh.urlshortener.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long userId;
    @Column(nullable = false,length = 20)
    private String username;
    @Column(nullable = false,length = 20)
    private String email;
    @Column(name = "password_hash",nullable = false,length = 20)
    private String password;
    @Enumerated(EnumType.STRING)
    private Role role=Role.USER;
    @Column(name = "created_at")
    private Instant createdAt;
    @Column(name = "updated_at")
    private Instant updatedAt;
    @PrePersist
    void onCreated(){
        Instant now= Instant.now();
        createdAt=now;
        updatedAt=now;
    }
    @PreUpdate
    void onUpdated(){
        updatedAt=Instant.now();
    }

}
