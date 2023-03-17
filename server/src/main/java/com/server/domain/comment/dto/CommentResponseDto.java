package com.server.domain.comment.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.server.domain.comment.entity.Comment;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class CommentResponseDto {
    private Long commentId;
    private Long parentId;
    private Long memberId;
    private String nickname;
    private String profileImageUrl;
    private String commentContent;
    private Comment.CommentStatus status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy년 MM월 dd일", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy년 MM월 dd일", timezone = "Asia/Seoul")
    private LocalDateTime modifiedAt;
    private List<CommentResponseDto> children;


    public static CommentResponseDto convertCommentToResponseDto(Comment comment){
        return CommentResponseDto.builder()
                .commentId(comment.getCommentId())
                .parentId(comment.getParent() == null ? null : comment.getParent().getCommentId())
                .memberId(comment.getMember().getMemberId())
                .nickname(comment.getMember().getNickName())
                .profileImageUrl(comment.getMember().getProfileImageUrl())
                .commentContent(comment.getCommentContent())
                .status(comment.getStatus())
                .createdAt(comment.getCreatedAt())
                .modifiedAt(comment.getModifiedAt())
                .children(new ArrayList<>())
                .build();
    }




}
