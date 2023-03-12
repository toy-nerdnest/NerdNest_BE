package com.server.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import javax.persistence.Column;
import java.util.List;

@Data
@AllArgsConstructor
public class ScrollResponseDto<T> {
    private boolean nextPage;
    private List<T> blogList;
}
