package com.server.domain.blog.mapper;


import com.server.domain.blog.dto.BlogDto;
import com.server.domain.blog.dto.BlogResponseDto;
import com.server.domain.blog.entity.Blog;
import com.server.domain.category.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BlogMapper {
    Blog blogPostDtoToBlog(BlogDto.Post blogPostDto, Category category);
    Blog blogPatchDtoToBlog(BlogDto.Patch blogPatchDto, Category category);

    @Mapping(target = "categoryId", expression = "java(blog.getCategory().getCategoryId())")
    BlogResponseDto blogToBlogResponseDto(Blog blog);
}
