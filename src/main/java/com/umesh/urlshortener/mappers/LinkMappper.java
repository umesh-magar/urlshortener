package com.umesh.urlshortener.mappers;

import com.umesh.urlshortener.dto.LinkDtos;
import com.umesh.urlshortener.entity.Link;

public class LinkMappper {
    public Link toEntity(LinkDtos.LinkCreateRequest request){
        return Link.builder()
                .originalUrl(request.originalUrl())
                .title(request.title())
                .maxClicks(request.maxClicks())
                .build();
    }
    public LinkDtos.LinkResponse toResponse(Link link){
        return null;
    }
}
