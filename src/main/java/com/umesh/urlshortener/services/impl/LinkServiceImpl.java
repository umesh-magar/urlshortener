package com.umesh.urlshortener.services.impl;

import com.umesh.urlshortener.config.ShortenerProperties;
import com.umesh.urlshortener.config.UrlValidator;
import com.umesh.urlshortener.dto.LinkDtos.LinkResponse;
import com.umesh.urlshortener.dto.LinkDtos.LinkCreateRequest;
import com.umesh.urlshortener.exceptions.FieldValidationException;
import com.umesh.urlshortener.exceptions.ForbiddenException;
import com.umesh.urlshortener.repository.LinkRepository;
import com.umesh.urlshortener.repository.UserRepository;
import com.umesh.urlshortener.services.LinkService;
import com.umesh.urlshortener.utils.ShortCodeGenerator;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.regex.Pattern;

@Service
public class LinkServiceImpl implements LinkService {
    private static final Pattern ALIAS =
            Pattern.compile("^[A-Za-z0-9_-]{3,32}$");
    private static final int MAX_CODE_ATTEMPTS=5;
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
    public LinkResponse createShortCode(Long userId,LinkCreateRequest linkRequestDto) {
        long currentLinks=linkRepository.countByUser_IdAndDeletedAtIsNull(userId);
        if(currentLinks>=props.maxLinksPerUser()) {
            throw new ForbiddenException("link limit reached(" + props.maxLinksPerUser() + "links per account)");
        }
        String originalUrl=urlValidator.validate(linkRequestDto.originalUrl());
        boolean custom=linkRequestDto.customAlias()!=null&& !linkRequestDto.customAlias().isBlank();
        String shortCode;
        if(custom){
            shortCode=linkRequestDto.customAlias().trim().toLowerCase(Locale.ROOT);
            validateAlias(shortCode);
        }

        return null;
    }
    private void validateAlias(String alias){
        if(!ALIAS.matcher(alias).matches()){
            throw new FieldValidationException("customAlias", "Alias must be 3-32 character: letters, digits,'-' and '_'");
        }
    }
    private String generateUniqueCode(){
        for(int attempt=0; attempt<MAX_CODE_ATTEMPTS; attempt++){
            String code=shortCodeGenerator.generate();
            return code;
        }
        throw  new IllegalStateException("could not generate the unique short code");
    }
    private String shortUrl(String code){
        String base=props.baseUrl();
        String normalizeBase=base.endsWith("/")?base.substring(0,base.length()-1):base;
        return normalizeBase+ "/" +code;
    }
}
