package com.umesh.urlshortener.services.impl;

import com.umesh.urlshortener.config.ShortenerProperties;
import com.umesh.urlshortener.config.UrlValidator;
import com.umesh.urlshortener.dto.LinkDtos;
import com.umesh.urlshortener.dto.LinkDtos.LinkResponse;
import com.umesh.urlshortener.dto.LinkDtos.LinkCreateRequest;
import com.umesh.urlshortener.entity.Link;
import com.umesh.urlshortener.entity.LinkStatus;
import com.umesh.urlshortener.entity.User;
import com.umesh.urlshortener.exceptions.ConflictException;
import com.umesh.urlshortener.exceptions.FieldValidationException;
import com.umesh.urlshortener.exceptions.ForbiddenException;
import com.umesh.urlshortener.exceptions.ResourceNotFoundException;
import com.umesh.urlshortener.mappers.LinkMapper;
import com.umesh.urlshortener.repository.LinkRepository;
import com.umesh.urlshortener.repository.UserRepository;
import com.umesh.urlshortener.services.LinkService;
import com.umesh.urlshortener.utils.ShortCodeGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Locale;
import java.util.regex.Pattern;

@Service
public class LinkServiceImpl implements LinkService {
    private static final Pattern ALIAS =
            Pattern.compile("^[A-Za-z0-9_-]{3,32}$");
    private static final int MAX_CODE_ATTEMPTS = 5;
    private final LinkRepository linkRepository;
    private final UserRepository userRepository;
    private final UrlValidator urlValidator;
    private final ShortenerProperties props;
    private final ShortCodeGenerator shortCodeGenerator;
    private final LinkMapper linkMapper;

    public LinkServiceImpl(LinkRepository linkRepository, UserRepository userRepository, UrlValidator urlValidator, ShortenerProperties props, ShortCodeGenerator shortCodeGenerator,LinkMapper linkMapper) {
        this.userRepository = userRepository;
        this.linkRepository = linkRepository;
        this.urlValidator = urlValidator;
        this.shortCodeGenerator = shortCodeGenerator;
        this.props = props;
        this.linkMapper=linkMapper;
    }

    @Override
    public LinkResponse createShortCode(Long userId, LinkCreateRequest linkRequestDto) {
        long currentLinks = linkRepository.countByUser_IdAndDeletedAtIsNull(userId);
        if (currentLinks >= props.maxLinksPerUser()) {
            throw new ForbiddenException("link limit reached(" + props.maxLinksPerUser() + "links per account)");
        }
        String originalUrl = urlValidator.validate(linkRequestDto.originalUrl());
        boolean custom = linkRequestDto.customAlias() != null && !linkRequestDto.customAlias().isBlank();
        String shortCode = "";
        if (custom) {
            shortCode = linkRequestDto.customAlias().trim().toLowerCase(Locale.ROOT);
            validateAlias(shortCode);
        }
        if (linkRepository.existsByShortCode(shortCode)) {
            throw new ConflictException("Alias is already taken");

    } else {
        shortCode = generateUniqueCode();
    }
        User user=userRepository.getReferenceById(userId);
        Link link=linkMapper.toEntity(linkRequestDto,user,shortCode,custom);
        Link savedLink=linkRepository.save(link);
        return linkMapper.toResponse(savedLink);
    }
    @Transactional(readOnly = true)
    public LinkResponse get(Long userId, Long id) {

        Link link = findOwned(userId, id);

        return linkMapper.toResponse(link);
    }

    @Transactional
    public LinkResponse update(
            Long userId,
            Long id,
            LinkDtos.LinkUpdateRequest req
    ) {

        Link link = findOwned(userId, id);

        link.setOriginalUrl(
                urlValidator.validate(req.originalUrl())
        );

        link.setTitle(blankToNull(req.title()));
        link.setExpiresAt(req.expiresAt());
        link.setMaxClicks(req.maxClicks());

        return linkMapper.toResponse(link);
    }
    @Transactional
    public LinkResponse changeStatus(
            Long userId,
            Long id,
            LinkStatus status
    ) {

        if (status == null) {
            throw new FieldValidationException(
                    "status",
                    "Status must not be null"
            );
        }

        Link link = findOwned(userId, id);

        link.setStatus(status);

        return linkMapper.toResponse(link);
    }
    @Transactional
    public void delete(Long userId, Long id) {

        Link link = findOwned(userId, id);

        link.setDeletedAt(Instant.now());
    }
    private Link findOwned(Long userId, Long id) {

        return linkRepository
                .findByIdAndUser_IdAndDeletedAtIsNull(id, userId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Link not found")
                );
    }

    private void validateAlias(String alias) {
        if (!ALIAS.matcher(alias).matches()) {
            throw new FieldValidationException("customAlias", "Alias must be 3-32 character: letters, digits,'-' and '_'");
        }
    }

    private String generateUniqueCode() {
        for (int attempt = 0; attempt < MAX_CODE_ATTEMPTS; attempt++) {
            String code = shortCodeGenerator.generate();
            return code;
        }
        throw new IllegalStateException("could not generate the unique short code");
    }

    private String shortUrl(String code) {
        String base = props.baseUrl();
        String normalizeBase = base.endsWith("/") ? base.substring(0, base.length() - 1) : base;
        return normalizeBase + "/" + code;
    }
    private static String blankToNull(String value) {

        return value == null || value.isBlank()
                ? null
                : value.trim();
    }
}
