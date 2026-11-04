package com.umesh.urlshortener.mappers;

import com.umesh.urlshortener.dto.AuthResponseDto;
import com.umesh.urlshortener.dto.RegisterRequestDto;
import com.umesh.urlshortener.entity.User;

public class AuthMapper {
    public User toEntity(RegisterRequestDto requestDto,String hashedPassword){
        return User.builder()
                .username(requestDto.getUsername())
                .email(requestDto.getEmail())
                .password(hashedPassword)
                .build();
    }
    public AuthResponseDto toResponse(User user){
        return AuthResponseDto.builder()
                .email(user.getEmail())
                .build();
    }
}
