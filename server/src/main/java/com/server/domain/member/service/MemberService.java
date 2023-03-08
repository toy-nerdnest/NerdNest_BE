package com.server.domain.member.service;

import com.server.domain.category.entity.Category;
import com.server.domain.category.repository.CategoryRepository;
import com.server.domain.category.service.CategoryService;
import com.server.domain.imageFile.service.ImageFileService;
import com.server.domain.member.entity.Member;
import com.server.domain.member.repository.MemberRepository;
import com.server.exception.BusinessLogicException;
import com.server.exception.ExceptionCode;
import com.server.security.utils.MemberAuthorityUtils;
import com.server.utils.CustomBeanUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {
    private final MemberRepository memberRepository;
    private final MemberAuthorityUtils authorityUtils;
    private final PasswordEncoder passwordEncoder;
    private final CustomBeanUtils customBeanUtils;
    private final ImageFileService imageFileService;

    private final CategoryRepository categoryRepository;

    // 회원 생성
    public Member createMember(Member member) {
        // 이메일 중복 확인
        verifyExistEmail(member.getEmail());

        String encryptedPassword = passwordEncoder.encode(member.getPassword());
        member.setPassword(encryptedPassword);
        member.setRoles(authorityUtils.createRole());

        member.setProfileImageUrl(imageFileService.getDefaultMemImgUrl());
        log.info("멤버 프로필에 기본이미지 저장");

        Member saveMember = memberRepository.save(member);
        //category 더미 추가
        List<Category> categoryList = new ArrayList<>();
        Category category = Category.builder()
                .categoryName("없음")
                .member(saveMember)
                .build();

        categoryList.add(category);
        categoryRepository.save(category);

        saveMember.setCategories(categoryList);

        return saveMember;
    }
    // 회원 정보 수정
    public Member updateMember(long memberId, Member member) {
        Member findMember = findVerifiedMember(memberId);

        Member updateMember = (Member) customBeanUtils.copyNonNullProperties(member, findMember);
        log.info("멤버 정보 수정 완료");
        return memberRepository.save(findMember);
    }

    // 회원 정보 가져오기
    public Member findMember(long memberId) {
        Member findMember = findVerifiedMember(memberId);
        return findMember;
    }

    public Member findMember(String nickname) {
        Member findMember = findVerifiedMember(nickname);
        return findMember;
    }

    private Member findVerifiedMember(String nickname) {
        Optional<Member> optionalMember =
                memberRepository.findByNickName(nickname);

        Member findMember =
                optionalMember.orElseThrow(
                        () -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND)
                );

        return findMember;
    }


    // 닉네임 중복 확인
    public void verifyExistNickName(String nickName) {
        Optional<Member> optionalMember =
                memberRepository.findByNickName(nickName);

        if (optionalMember.isPresent()) {
            throw new BusinessLogicException(ExceptionCode.MEMBER_EXISTS);
        }
    }

    public void verifyExistEmail(String email) {
        Optional<Member> optionalMember =
                memberRepository.findByEmail(email);

        if (optionalMember.isPresent()) {
            throw new BusinessLogicException(ExceptionCode.MEMBER_EXISTS);
        }
    }

    public Member findVerifiedMember(long memberId) {
        Optional<Member> optionalMember =
                memberRepository.findById(memberId);

        Member findMember =
                optionalMember.orElseThrow(
                        () -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND)
                );

        return findMember;
    }
}
