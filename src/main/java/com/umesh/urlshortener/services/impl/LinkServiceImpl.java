package com.umesh.urlshortener.services.impl;

import com.umesh.urlshortener.config.ShortenerProperties;
import com.umesh.urlshortener.config.UrlValidator;
import com.umesh.urlshortener.dto.LinkRequestDto;
import com.umesh.urlshortener.dto.LinkResponseDto;
import com.umesh.urlshortener.repository.LinkRepository;
import com.umesh.urlshortener.repository.UserRepository;
import com.umesh.urlshortener.services.LinkService;
import org.springframework.stereotype.Service;

@Service
public class LinkServiceImpl implements LinkService {
    private final LinkRepository linkRepository;
    private final UserRepository userRepository;
    private final UrlValidator urlValidator;
    private final ShortenerProperties props;
    public LinkServiceImpl(LinkRepository linkRepository, UserRepository userRepository, UrlValidator urlValidator, ShortenerProperties props){
        this.userRepository=userRepository;
        this.linkRepository=linkRepository;
        this.urlValidator=urlValidator;
        this.props=props;
    }

    @Override
    public LinkResponseDto createShortCode(LinkRequestDto linkRequestDto) {

        return null;
    }
}
