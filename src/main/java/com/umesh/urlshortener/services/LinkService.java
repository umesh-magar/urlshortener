package com.umesh.urlshortener.services;

import com.umesh.urlshortener.dto.LinkDtos;

public interface LinkService {
    LinkDtos.LinkResponse createShortCode(Long userId, LinkDtos.LinkCreateRequest linkRequestDto);
}
