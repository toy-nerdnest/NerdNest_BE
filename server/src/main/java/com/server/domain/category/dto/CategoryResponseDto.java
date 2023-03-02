package com.server.domain.category.dto;

import com.server.domain.blog.entity.Blog;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class CategoryResponseDto {
    private Long categoryId;
    private String categoryName;
    private List<Blog> blogList;
}
