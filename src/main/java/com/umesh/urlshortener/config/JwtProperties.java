package com.umesh.urlshortener.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties (String secretKey, long accessExpirationSecretKey){
    public JwtProperties{
        if(secretKey==null || secretKey.isBlank()){
            throw new IllegalStateException("app.jwt.secret must be configured");
        }
        if(accessExpirationSecretKey<=0){
            throw new IllegalStateException("Access token expired time must be positive");
        }
    }

}
