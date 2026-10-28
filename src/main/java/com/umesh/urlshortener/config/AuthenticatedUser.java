package com.umesh.urlshortener.config;

import com.umesh.urlshortener.entity.Role;

public record AuthenticatedUser(Long id, String email, Role role) {
}
