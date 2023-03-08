package com.server.domain.likes.controller;

import com.server.domain.blog.entity.Blog;
import com.server.domain.blog.service.BlogService;
import com.server.domain.likes.dto.LikeDto;
import com.server.domain.likes.service.LikeService;
import com.server.domain.member.entity.Member;
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

    @PostMapping("/like/{blog-Id}")
    public ResponseEntity insert(@AuthenticationPrincipal Member loginMember,
                                  @PathVariable("blog-Id") Long blogId) {
        // '좋아요'가 있으면 true, 없으면 false 반환
        Blog blog = blogService.findBlogById(blogId);
        boolean like = likeService.findLike(loginMember, blog);

        if (like) {
            likeService.deleteLike(loginMember, blog);
        } else {
            likeService.addLike(loginMember, blog);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

}

