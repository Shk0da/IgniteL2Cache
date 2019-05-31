package com.github.shk0da.demo.model;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageableRequest extends PageRequest implements Pageable {

    public PageableRequest(int page, int size) {
        super(page - 1, size);
    }

    public PageableRequest(int page, int size, Sort.Direction direction, String... properties) {
        super(page - 1, size, direction, properties);
    }

    public PageableRequest(int page, int size, Sort sort) {
        super(page - 1, size, sort);
    }
}
