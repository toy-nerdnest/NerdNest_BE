package com.server.domain.member.controller;

import com.server.domain.member.dto.MemberDto;
import com.server.domain.member.entity.Member;
import com.server.domain.member.mapper.MemberMapper;
import com.server.domain.member.service.MemberService;
import com.server.response.SingleResponseDto;
import com.server.security.utils.MemberDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.net.URI;

@RestController
@RequestMapping
@Valid
@RequiredArgsConstructor
@Slf4j
public class MemberController {
    private final MemberService memberService;
    private final MemberMapper mapper;

    // 회원가입 -> post
    @PostMapping("/signup")
    public ResponseEntity signupMember(@Valid @RequestBody MemberDto.Post memberPostDto) {
        // memberservice에 회원 생성 로직 타게 만들기
        Member member = memberService.createMember(mapper.memberPostDtoToMember(memberPostDto));
        // 반환 값 -> url, http code 201
        URI location = UriComponentsBuilder.newInstance()
                .path("/members/{member-id}")
                .buildAndExpand(member.getMemberId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    // 내 정보 수정하기 -> patch, 권한 필요
    @PatchMapping("/members/{member-id}")
    public ResponseEntity patchMemberImage(@Positive @PathVariable("member-id") long memberId,
                                           @Valid @RequestBody MemberDto.Patch memberPatchDto,
                                           @AuthenticationPrincipal Member loginMember) {
        if(loginMember == null) {
            log.error("허용되지 않은 접근입니다.");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else if(loginMember.getMemberId() != memberId) {
            log.error("수정할 memberId : {}, 로그인한 membmerId :{}", memberId, loginMember.getMemberId());
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Member member = memberService.updateMember(memberId, mapper.memberPatchDtoToMember(memberPatchDto));
        MemberDto.Response response = mapper.memberToMemberResponseDto(member);

        return new ResponseEntity<>(new SingleResponseDto<>(response), HttpStatus.OK);
    }

    // 닉네임 중복 확인 -> get
    @GetMapping("/members/check")
    public ResponseEntity checkForDuplicateNickName(@RequestParam("nickname") String nickName) {
        // 쿼리로 받은 닉네임을 memberService에 닉네임 중복 확인 메서드에 보내기
        memberService.verifyExistNickName(nickName);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 회원 정보 가져오기
    @GetMapping("/members/{member-id}")
    public ResponseEntity getMemberImage(@Positive @PathVariable("member-id") long memberId) {


        Member member = memberService.findMember(memberId);
        MemberDto.Response response = mapper.memberToMemberResponseDto(member);

        return new ResponseEntity<>(new SingleResponseDto<>(response), HttpStatus.OK);
    }


}
