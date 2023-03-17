package com.server.domain.category.dto;

import com.server.domain.blog.dto.BlogResponseDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class CategoryResponseDto {
    private Long categoryId;
    private String categoryName;

    @Data
    @Builder
    public static class Single {
        private List<BlogResponseDto.Member> blogList;
    }

}
