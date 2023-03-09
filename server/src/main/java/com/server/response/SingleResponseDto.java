package com.server.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SingleResponseDto<T> {
    private T data;

    @Getter
    @AllArgsConstructor
    public static class Category<T> {
        private T categoryList;
    }

    @Getter
    @AllArgsConstructor
    public static class Like<T> {
        private T isLike;
    }
}
