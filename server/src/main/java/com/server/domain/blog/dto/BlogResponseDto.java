package com.server.domain.blog.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.server.domain.comment.entity.Comment;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class BlogResponseDto {

    private String blogTitle;
    private String blogContent;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy년 MM월 dd일", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;
    private String categoryName;

//    private List<Comment> commentList;

    @Builder
    @Data
    public static class WithCategory {
        private long blogId;
        private String titleImageUrl;
        private String blogTitle;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy년 MM월 dd일", timezone = "Asia/Seoul")
        private LocalDateTime createdAt;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy년 MM월 dd일", timezone = "Asia/Seoul")
        private LocalDateTime modifiedAt;
//        private int commentCount;
//        private int likeCount;
    }

}
