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

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BlogMapper {

    /* 블로그 등록 */
    @Mapping(target = "category", source = "category")
    Blog blogPostDtoToBlog(BlogDto.Post blogPostDto, Category category, Member member);

    /* 블로그 수정 */
    @Mapping(target = "category", source = "category")
    Blog blogPatchDtoToBlog(BlogDto.Patch blogPatchDto, Category category);

    /* 블로그 조회 */
    @Mapping(target = "categoryId", expression = "java(blog.getCategory().getCategoryId())")
    BlogResponseDto blogToBlogResponseDto(Blog blog);

    /* 홈 화면 데이터 */
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

    /* 멤버 개인 블로그 데이터 */
    List<BlogResponseDto.Member> blogListToBlogResponseMemberDto(List<Blog> blogs);

    /* 블로그 상세 데이터 */
    @Mapping(target = "categoryId", expression = "java(blog.getCategory().getCategoryId())")
    @Mapping(target = "commentList", source = "commentResponseDtos")
    BlogResponseDto.Detail blogListToBlogDetailResponseDtoWithComment(Blog blog, List<CommentResponseDto> commentResponseDtos);

}
