package com.server.domain.blog.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BlogResponseDto {

    private String titleImageUrl;
    private String blogTitle;
    private String blogContent;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy년 MM월 dd일", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;
    private Long categoryId;

//    private List<Comment> commentList;

    @Builder
    @Data
    public static class WithCategory {
        private long blogId;
        private String titleImageUrl;
        private String blogTitle;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy년 MM월 dd일 hh시 mm분 ", timezone = "Asia/Seoul")
        private LocalDateTime createdAt;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy년 MM월 dd일 hh시 mm분", timezone = "Asia/Seoul")
        private LocalDateTime modifiedAt;
//        private int commentCount;
        private int likeCount;
    }

    @Builder
    @Data
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
//        private int commentCount; //TODO
    }

}
