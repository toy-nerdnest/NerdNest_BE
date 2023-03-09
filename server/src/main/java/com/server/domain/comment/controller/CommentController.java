package com.server.domain.comment.controller;

import com.server.domain.blog.service.BlogService;
import com.server.domain.comment.dto.CommentDto;
import com.server.domain.comment.entity.Comment;
import com.server.domain.comment.service.CommentService;
import com.server.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final BlogService blogService;

    @PostMapping("/comments")
    public ResponseEntity postComment(@AuthenticationPrincipal Member loginMember,
                                      @RequestBody @Valid CommentDto commentDto) {
        // TODO: 로그인 유저 로직 추가
        if (loginMember == null) {
            log.error("loginMember is null : 허용되지 않은 접근입니다.");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Comment.CommentBuilder commentBuilder = Comment.builder();
        Comment comment =
                commentService.createComment(
                        commentBuilder
                                .commentContent(commentDto.getCommentContent())
                                .member(loginMember)
                                .blog(blogService.findBlogById(commentDto.getBlogId()))
                                .status(Comment.CommentStatus.ALIVE)
                                .build()
                        , commentDto.getParentId());

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PatchMapping("/comments/{comment-id}")
    public ResponseEntity postComment(@AuthenticationPrincipal Member loginMember,
                                      @PathVariable("comment-id") @Positive long commentId,
                                      @RequestBody @Valid CommentDto.Patch commentPatchDto) {
        if (loginMember == null) {
            log.error("loginMember is null : 허용되지 않은 접근입니다.");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        // TODO: memberId 비교로직
        Comment comment = commentService.findComment(commentId);
        comment.setCommentContent(commentPatchDto.getCommentContent());

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/delete/{comment-id}")
    public ResponseEntity<HttpStatus> deleteComment(@AuthenticationPrincipal Member loginMember,
                                                    @PathVariable("comment-id") @Positive long commentId) {
        commentService.verifyOwner(commentId, loginMember);
        Comment comment = commentService.findComment(commentId);
        commentService.deleteComment(comment);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
