package com.server.domain.blog.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.server.domain.comment.dto.CommentResponseDto;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class BlogResponseDto {
    private String titleImageUrl;
    private String blogTitle;
    private String blogContent;
    private Long categoryId;

    @Data
    @Builder
    public static class Member {
        private long blogId;
        private String titleImageUrl;
        private String blogTitle;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy년 MM월 dd일 hh시 mm분 ", timezone = "Asia/Seoul")
        private LocalDateTime createdAt;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy년 MM월 dd일 hh시 mm분", timezone = "Asia/Seoul")
        private LocalDateTime modifiedAt;
        private int likeCount;
        private int commentCount;
    }

    @Data
    @Builder
    public static class Home {
        private Long memberId;
        private Long blogId;
        private String titleImageUrl;
        private String blogTitle;
        private String blogContent;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy년 MM월 dd일 hh시 mm분", timezone = "Asia/Seoul")
        private LocalDateTime createdAt;
        private String writer; // 작성자는 nickname과 동일
        private int likeCount;
        private int commentCount;
    }

    @Builder
    @Data
    public static class Detail {
        private String titleImageUrl;
        private String blogTitle;
        private String blogContent;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy년 MM월 dd일 hh시 mm분", timezone = "Asia/Seoul")
        private LocalDateTime createdAt;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy년 MM월 dd일 hh시 mm분", timezone = "Asia/Seoul")
        private LocalDateTime modifiedAt;
        private Long categoryId;
        private int commentCount;
        private List<CommentResponseDto> commentList;
    }

    @Builder
    @Data
    public static class Record {
        private Long blogId;
        private int blogRecord;
    }
}
