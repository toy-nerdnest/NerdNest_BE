package com.server.domain.imageFile.controller;

import com.server.domain.imageFile.dto.ImageFileResponseDto;
import com.server.domain.imageFile.entity.ImageFile;
import com.server.domain.imageFile.mapper.ImageFileMapper;
import com.server.domain.imageFile.service.ImageFileService;
import com.server.domain.member.entity.Member;
import com.server.domain.member.service.MemberService;
import com.server.response.SingleResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/s3")
@RequiredArgsConstructor
public class ImageFileController {

    private final ImageFileService imageFileService;
    private final MemberService memberService;
    private final ImageFileMapper mapper;
    // 멤버 프로필 이미지 업로드
    @PostMapping("/member")
    public ResponseEntity uploadMemberImg(@RequestParam("image")MultipartFile multipartFile,
                                          @AuthenticationPrincipal Member loginMember) throws IOException {
        Member findMember = memberService.findMember(loginMember.getMemberId());
        ImageFile imageFile = imageFileService.uploadMemImg(findMember, multipartFile);

        ImageFileResponseDto response = mapper.imageFileToImageFileResponseDto(imageFile);

        return new ResponseEntity<>(new SingleResponseDto<>(response), HttpStatus.OK);
    }
    // blog 타이틀 이미지 업로드
    @PostMapping("/blog")
    public ResponseEntity uploadBlogTitleImg (@RequestParam("image")MultipartFile multipartFile,
                                              @AuthenticationPrincipal Member loginMember) throws IOException {
        Member findMember = memberService.findMember(loginMember.getMemberId());
        ImageFile imageFile = imageFileService.uploadBlogTitleImg(multipartFile, findMember);

        ImageFileResponseDto response = mapper.imageFileToImageFileResponseDto(imageFile);

        return new ResponseEntity<>(new SingleResponseDto<>(response), HttpStatus.OK);
    }
}
