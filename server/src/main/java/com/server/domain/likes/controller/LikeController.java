package com.server.domain.likes.controller;

import com.server.domain.likes.dto.LikeResponseDto;
import com.server.domain.likes.entity.Likes;
import com.server.domain.likes.mapper.LikeMapper;
import com.server.domain.likes.service.LikeService;
import com.server.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Positive;

@Slf4j
@RestController
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;
    private final LikeMapper mapper;

    @PostMapping("/blogs/{blog-Id}/likes")
    public ResponseEntity insert(@AuthenticationPrincipal Member loginMember,
                                 @Positive @PathVariable("blog-Id") long blogId) {

        if(loginMember == null) {
            log.error("loginMember is null : 허용되지 않은 접근입니다.");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Likes likes = likeService.likeBlogs(loginMember, blogId);
        LikeResponseDto response = mapper.likeToLikeResponseDto(likes);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}

