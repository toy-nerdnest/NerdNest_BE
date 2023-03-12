package com.server.response;

import lombok.*;

import javax.persistence.Column;

@Getter
@AllArgsConstructor
public class PageInfo {
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Judgement{
        private boolean isFinal = true;
    }
}
