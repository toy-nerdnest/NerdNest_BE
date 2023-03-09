package com.server.domain.likes.mapper;

import com.server.domain.likes.dto.LikeResponseDto;
import com.server.domain.likes.entity.Like;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LikeMapper {
    default LikeResponseDto likeToLikeResponseDto(Like like) {
        if(like == null) {
            return null;
        }
        LikeResponseDto responseDto = LikeResponseDto.builder()
                .memberId(like.getMember().getMemberId())
                .blogId(like.getBlog().getBlogId())
                .likeStatus(like.getLikeStatus())
                .build();

        return responseDto;
    }
}
