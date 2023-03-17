package com.server.domain.likes.mapper;

import com.server.domain.blog.entity.Blog;
import com.server.domain.likes.dto.LikeResponseDto;
import com.server.domain.likes.dto.MyLikeBlogResponseDto;
import com.server.domain.likes.entity.Likes;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LikeMapper {
    default LikeResponseDto likeToLikeResponseDto(Likes likes) {
        if(likes == null) {
            return null;
        }
        LikeResponseDto responseDto = LikeResponseDto.builder()
                .memberId(likes.getMember().getMemberId())
                .blogId(likes.getBlog().getBlogId())
                .likeStatus(likes.getLikeStatus())
                .build();

        return responseDto;
    }

    default MyLikeBlogResponseDto likeToMyLikeBlogResponseDto(Likes likes) {
        if(likes == null) {
            return  null;
        }

        Blog blog = likes.getBlog();

        MyLikeBlogResponseDto response = MyLikeBlogResponseDto.builder()
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

        return response;
    }

    List<MyLikeBlogResponseDto> likesToMyLikeBlogResponseDtos(List<Likes> likes);
}
