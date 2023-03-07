package com.server.domain.blog.mapper;


import com.server.domain.blog.dto.BlogDto;
import com.server.domain.blog.dto.BlogResponseDto;
import com.server.domain.blog.entity.Blog;
import com.server.domain.category.entity.Category;
import com.server.domain.member.entity.Member;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;
import java.util.Optional;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BlogMapper {
    Blog blogPostDtoToBlog(BlogDto.Post blogPostDto, Category category, Member member);
    Blog blogPatchDtoToBlog(BlogDto.Patch blogPatchDto, Category category);
//    @Mapping(target = "categoryId", expression = "java(blog.getCategory().getCategoryId())")
    @Mapping(target = "categoryName", expression = "java(blog.getCategory().getCategoryName())")
    BlogResponseDto blogToBlogResponseDto(Blog blog);
    List<BlogResponseDto.WithCategory> blogListToBlogResponseDtoWithCategory(List<Blog> blogs);
}
