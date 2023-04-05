package com.server.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class SingleResponseDto<T> {
    private T data;

    @Getter
    @AllArgsConstructor
    public static class Category<T> {
        private T categoryList;
    }
}
