package com.server.domain.likes.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MyLikeBlogResponseDto {
    private Long memberId;
    private Long blogId;
    private String titleImageUrl;
    private String blogTitle;
    private String blogContent;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy년 MM월 dd일 hh시 mm분", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;
    private String writer;
    private int likeCount;
    private int commentCount;
}
