package com.server.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PageInfo {
    private int page;
    private int size;
    private int totalElements;
    private int totalPages;

    public PageInfo(int page, int size, int totalElements, int totalPages) {
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }
}
