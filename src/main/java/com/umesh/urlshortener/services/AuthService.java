package com.umesh.urlshortener.services;

import com.umesh.urlshortener.dto.AuthResponseDto;
import com.umesh.urlshortener.dto.RegisterRequestDto;

public interface AuthService {
    AuthResponseDto register(RegisterRequestDto requestDto);
}
