package com.server.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ListResponseDto<T>  {
    private List<T> blogList;
}
