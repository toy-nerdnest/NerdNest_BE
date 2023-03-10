package com.server.domain.likes.mapper;

import com.server.domain.likes.dto.LikeResponseDto;
import com.server.domain.likes.entity.Likes;
import org.mapstruct.Mapper;

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
}
