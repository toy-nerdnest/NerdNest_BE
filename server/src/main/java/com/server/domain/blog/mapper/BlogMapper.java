package com.server.domain.blog.mapper;


import com.server.domain.blog.dto.BlogDto;
import com.server.domain.blog.dto.BlogResponseDto;
import com.server.domain.blog.entity.Blog;
import com.server.domain.category.entity.Category;
import com.server.domain.comment.dto.CommentResponseDto;
import com.server.domain.member.entity.Member;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BlogMapper {
    @Mapping(target = "category", source = "category")
    Blog blogPostDtoToBlog(BlogDto.Post blogPostDto, Category category, Member member);
    @Mapping(target = "category", source = "category")
    Blog blogPatchDtoToBlog(BlogDto.Patch blogPatchDto, Category category);
//    @Mapping(target = "categoryId", expression = "java(blog.getCategory().getCategoryId())")
    @Mapping(target = "categoryId", expression = "java(blog.getCategory().getCategoryId())")
    BlogResponseDto blogToBlogResponseDto(Blog blog);

    default List<BlogResponseDto.WithCategory> blogListToBlogResponseDtoWithCategory(List<Blog> blogs) {
        return blogs.stream().map(blog -> {
            return BlogResponseDto.WithCategory.builder()
                    .blogId(blog.getBlogId())
                    .titleImageUrl(blog.getTitleImageUrl())
                    .blogTitle(blog.getBlogTitle())
                    .createdAt(blog.getCreatedAt())
                    .modifiedAt(blog.getModifiedAt())
                    .likeCount(blog.getLikeCount())
                    .commentCount(blog.getCommentCount())
                    .build();
        }).collect(Collectors.toList());
    }

    default List<BlogResponseDto.Home> blogListToBlogResponseHomeDto(List<Blog> blogs) {
        return blogs.stream().map(blog -> {
            return BlogResponseDto.Home.builder()
                    .memberId(blog.getMember().getMemberId())
                    .blogId(blog.getBlogId())
                    .titleImageUrl(blog.getTitleImageUrl())
                    .blogTitle(blog.getBlogTitle())
                    .blogContent(blog.getBlogContent())
                    .createdAt(blog.getCreatedAt())
                    .writer(blog.getMember().getNickName())
                    .likeCount(blog.getLikeCount())
                    .commentCount(blog.getCommentCount())
                    .build();
        }).collect(Collectors.toList());

    }

    default BlogResponseDto.WithComment blogListToBlogResponseDtoWithComment(Blog blog, List<CommentResponseDto> commentResponseDtos) {
        return BlogResponseDto.WithComment.builder()
                .titleImageUrl(blog.getTitleImageUrl())
                .blogTitle(blog.getBlogTitle())
                .createdAt(blog.getCreatedAt())
                .categoryId(blog.getCategory().getCategoryId())
                .commentList(commentResponseDtos)
                .build();
    }

}
