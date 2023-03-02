package com.server.domain.category.mapper;


import com.server.domain.category.dto.CategoryDto;
import com.server.domain.category.dto.CategoryResponseDto;
import com.server.domain.category.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CategoryMapper {
    Category categoryDtoPostToCategory(CategoryDto.Post categoryDtoPost);

    Category categoryDtoPatchToCategory(CategoryDto.Patch categoryDtoPatch);

    default CategoryResponseDto categoryToCategoryResponseDto(Category category) {
        return CategoryResponseDto.builder()
                .categoryName(category.getCategoryName())
                .categoryId(category.getCategoryId())
                .blogList(category.getBlogList())
                .build();
    }
}
