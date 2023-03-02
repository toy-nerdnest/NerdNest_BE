package com.server.domain.blog.mapper;


import com.server.domain.blog.dto.BlogDto;
import com.server.domain.blog.entity.Blog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BlogMapper {
    Blog blogPostDtoToBlog(BlogDto.Post blogPostDto);
}
