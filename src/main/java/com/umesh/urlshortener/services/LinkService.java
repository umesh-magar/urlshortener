package com.umesh.urlshortener.services;

import com.umesh.urlshortener.dto.LinkRequestDto;
import com.umesh.urlshortener.dto.LinkResponseDto;

public interface LinkService {
    LinkResponseDto createShortCode(LinkRequestDto linkRequestDto);
}
