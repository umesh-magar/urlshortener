package com.umesh.urlshortener.mappers;

import com.umesh.urlshortener.dto.LinkDtos;
import com.umesh.urlshortener.entity.Link;
import com.umesh.urlshortener.entity.LinkStatus;
import com.umesh.urlshortener.entity.User;

public class LinkMapper {
    public Link toEntity(LinkDtos.LinkCreateRequest request, User user,String shortCode,boolean custom){
        return Link.builder()
                .shortCode(shortCode)
                .originalUrl(request.originalUrl())
                .title(request.title())
                .customAlias(custom)
                .maxClicks(request.maxClicks())
                .user(user)
                .status(LinkStatus.ACTIVE)
                .build();
    }
    public LinkDtos.LinkResponse toResponse(Link link){
        return null;
    }
}
