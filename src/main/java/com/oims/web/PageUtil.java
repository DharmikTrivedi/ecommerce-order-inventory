package com.oims.web;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Set;

public final class PageUtil {

    private PageUtil() {
    }

    /**
     * Builds a Pageable from request params. Only whitelisted sort fields are accepted,
     * anything else falls back to the default so users can't sort on random properties.
     */
    public static Pageable build(int page, int size, String sort, String dir, Set<String> allowed, String defaultSort) {
        String field = (sort != null && allowed.contains(sort)) ? sort : defaultSort;
        Sort.Direction direction = "desc".equalsIgnoreCase(dir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        return PageRequest.of(Math.max(page, 0), size, Sort.by(direction, field));
    }
}
