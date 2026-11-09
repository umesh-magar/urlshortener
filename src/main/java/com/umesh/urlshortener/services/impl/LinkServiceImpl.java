package com.umesh.urlshortener.services.impl;

import com.umesh.urlshortener.config.ShortenerProperties;
import com.umesh.urlshortener.config.UrlValidator;
import com.umesh.urlshortener.dto.LinkDtos.LinkResponse;
import com.umesh.urlshortener.dto.LinkDtos.LinkCreateRequest;
import com.umesh.urlshortener.repository.LinkRepository;
import com.umesh.urlshortener.repository.UserRepository;
import com.umesh.urlshortener.services.LinkService;
import com.umesh.urlshortener.utils.ShortCodeGenerator;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class LinkServiceImpl implements LinkService {
    private static final Pattern ALIAS =
            Pattern.compile("^[A-Za-z0-9_-]{3,32}$");
    private final LinkRepository linkRepository;
    private final UserRepository userRepository;
    private final UrlValidator urlValidator;
    private final ShortenerProperties props;
    private final ShortCodeGenerator shortCodeGenerator;
    public LinkServiceImpl(LinkRepository linkRepository, UserRepository userRepository, UrlValidator urlValidator, ShortenerProperties props,ShortCodeGenerator shortCodeGenerator){
        this.userRepository=userRepository;
        this.linkRepository=linkRepository;
        this.urlValidator=urlValidator;
        this.shortCodeGenerator=shortCodeGenerator;
        this.props=props;
    }

    @Override
    public LinkResponse createShortCode(LinkCreateRequest linkRequestDto) {

        return null;
    }
}
