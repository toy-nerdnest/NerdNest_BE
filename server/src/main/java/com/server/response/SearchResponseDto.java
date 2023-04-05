package com.server.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class SearchResponseDto<T> {
    private boolean nextPage;
    private List<T> blogList;
    private long totalElements;
}
