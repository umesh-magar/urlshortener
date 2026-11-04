package com.umesh.urlshortener.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponseDto {
    private String email;
    private String accessToken;
    private String RefreshToken;
}
