package com.server.domain.likes.controller;

import com.server.domain.blog.dto.BlogResponseDto;
import com.server.domain.blog.entity.Blog;
import com.server.domain.blog.service.BlogService;
import com.server.domain.likes.dto.LikeResponseDto;
import com.server.domain.likes.entity.Like;
import com.server.domain.likes.mapper.LikeMapper;
import com.server.domain.likes.service.LikeService;
import com.server.domain.member.entity.Member;
import com.server.response.ListResponseDto;
import com.server.response.SingleResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;
    private final LikeMapper mapper;

    @PostMapping("/blogs/{blog-Id}/likes")
    public ResponseEntity insert(@AuthenticationPrincipal Member loginMember,
                                 @Positive @PathVariable("blog-Id") Long blogId) {

        if(loginMember == null) {
            log.error("loginMember is null : 허용되지 않은 접근입니다.");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Like like = likeService.likeBlogs(loginMember, blogId);
        LikeResponseDto response = mapper.likeToLikeResponseDto(like);

        return new ResponseEntity<>(new SingleResponseDto<>(response), HttpStatus.OK);
    }

}

