package com.umesh.urlshortener.dto;

import com.umesh.urlshortener.entity.LinkState;
import com.umesh.urlshortener.entity.LinkStatus;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LinkResponseDto {
    private Integer linkId;
    private String shortCode;
    private String shortUrl;
    private String originalUrl;
    private String title;
    LinkStatus linkStatus;
    LinkState linkState;
    Long maxClicks;
    long clickCount;
}
