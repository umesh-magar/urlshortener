package com.umesh.urlshortener.config;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app.shortener")
public record ShortenerProperties(
        @NotBlank
        String baseUrl,
        @Min(6)
        @Max(16)
        int codeLength,
        boolean blockPrivateHosts,
        @Min(1)
        int maxLinksPerUser
) {

}
