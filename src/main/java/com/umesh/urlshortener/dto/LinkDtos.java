package com.umesh.urlshortener.dto;

import com.umesh.urlshortener.entity.LinkState;
import com.umesh.urlshortener.entity.LinkStatus;
import jakarta.validation.constraints.*;

import java.time.Instant;

public class LinkDtos {
    private LinkDtos(){};
    public record  LinkCreateRequest(
            @NotBlank(message = "original url is required")
            @Size(max = 2048,message = "URL cannot exceeds 2000 character")
            String originalUrl,
            @Size(max = 32,message = "Custom alias cannot exceed 32 character")
            String customAlias,
            @Size(max = 150, message = "Title cannot exceed 150 character")
            String title,
            @Future(message = "Expiry must be in future")
            Instant expiresAt,
            @Min(value = 1, message = "Max clicks must be at least 1")
            Long maxClicks
    ){};
    public record LinkUpdateRequest(
            @NotBlank(message = "Original URL is required")
            @Size(max = 2048, message = "URL cannot exceeds 2000 character")
            String originalUrl,
            @Size(max = 150, message = "Title cannot exceeds 150 character")
            String title,
            @Future(message = "Expiry must be in future")
            Instant expiresAt,
            @Min(value = 1, message ="Max clicks must be at least 1")
            Long maxClicks
    ){};
    public record LinkStatusRequest(
            @NotNull(message = "link status required")
            LinkState status
    ){};
    public record LinkResponse(
            Long linkId,
            String originalUrl,
            String shortCode,
            String shortUrl,
            String title,
            boolean customAlias,
            LinkStatus linkStatus,
            Instant expiresAt,
            Long maxClicks,
            long clickCount,
            Instant createdAt,
            Instant updatedAt
    ){};
}
