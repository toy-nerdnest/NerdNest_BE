package com.server.domain.likes.controller;

import com.server.domain.blog.entity.Blog;
import com.server.domain.blog.service.BlogService;
import com.server.domain.likes.dto.LikeResponseDto;
import com.server.domain.likes.service.LikeService;
import com.server.domain.member.entity.Member;
import com.server.response.SingleResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;
    private final BlogService blogService;

    @PostMapping("/blogs/{blog-Id}/likes")
    public ResponseEntity insert(@AuthenticationPrincipal Member loginMember,
                                 @PathVariable("blog-Id") Long blogId) {
        // '좋아요'가 있으면 삭제, 없으면 추가
        Blog blog = blogService.findBlogById(blogId);
        boolean isLike = likeService.findLike(loginMember, blog);

        if (isLike) {
            likeService.deleteLike(loginMember, blog);
        } else {
            likeService.addLike(loginMember, blog);
        }

        // 리턴 바디값은 블로그 내 '좋아요'가 있으면 true, 없으면 false
        return new ResponseEntity<>(new SingleResponseDto.Like<>(!isLike), HttpStatus.OK);
    }

}

