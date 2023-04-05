package com.server.domain.category.mapper;


import com.server.domain.blog.dto.BlogResponseDto;
import com.server.domain.blog.entity.Blog;
import com.server.domain.category.dto.CategoryDto;
import com.server.domain.category.dto.CategoryResponseDto;
import com.server.domain.category.entity.Category;
import com.server.domain.member.entity.Member;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CategoryMapper {

    Category categoryDtoPostToCategory(CategoryDto.Post categoryDtoPost, Member member);

    Category categoryDtoPatchToCategory(CategoryDto.Patch categoryDtoPatch);
    List<CategoryResponseDto> categoriesToCategoryResponseDto(List<Category> categories);
    default CategoryResponseDto.Single categoryToCategorySingleResponseDto(List<Blog> blogs) {
        return CategoryResponseDto.Single.builder()
                .blogList(blogs.stream().map(blog -> {
                    return BlogResponseDto.Member.builder()
                            .blogId(blog.getBlogId())
                            .titleImageUrl(blog.getTitleImageUrl())
                            .blogTitle(blog.getBlogTitle())
                            .createdAt(blog.getCreatedAt())
                            .modifiedAt(blog.getModifiedAt())
                            .build();
                }).collect(Collectors.toList()))
                .build();
    }
    CategoryResponseDto categoryToCategoryResponseDto(Category category);
}
