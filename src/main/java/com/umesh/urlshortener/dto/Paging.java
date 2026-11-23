package com.umesh.urlshortener.dto;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class Paging {
    private static final int MAX_SIZE = 100;

    private Paging() {
    }

    /** sort: newest (default) | oldest | clicks (most clicked first). */
    public static Pageable ofLinks(int page, int size, String sort) {
        String key = sort == null ? "newest" : sort.trim().toLowerCase();
        Sort s = switch (key) {
            case "oldest" -> Sort.by(Sort.Order.asc("createdAt"));
            case "clicks" -> Sort.by(Sort.Order.desc("clickCount"), Sort.Order.desc("createdAt"));
            default -> Sort.by(Sort.Order.desc("createdAt"));
        };
        return PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), MAX_SIZE), s);
    }
}
