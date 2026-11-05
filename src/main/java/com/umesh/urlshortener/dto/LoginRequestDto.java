package com.umesh.urlshortener.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginRequestDto {
    @NotBlank(message = "email should required")
    private String email;
    @NotBlank(message = "password required")
    private String password;
}
