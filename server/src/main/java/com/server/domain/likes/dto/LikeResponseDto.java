package com.server.domain.likes.dto;

import com.server.domain.likes.entity.Like;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;

@Data
@Builder
public class LikeResponseDto {
    private long memberId; // 좋아요 한 멤버
    private long blogId; // 좋아요 누른 블로그
    private Like.LikeStatus likeStatus; // 좋아요 상태
}
