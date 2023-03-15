package com.server.security.oauth.service;

import com.server.domain.member.entity.Member;
import com.server.domain.member.repository.MemberRepository;
import com.server.exception.BusinessLogicException;
import com.server.exception.ExceptionCode;
import com.server.security.oauth.utils.OAuthAttributes;
import com.server.security.utils.MemberAuthorityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class MemberOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    // 사용자의 정보를 기반으로 가입 및 정보 저장 기능을 한다.
    private final MemberRepository memberRepository;
    private final MemberAuthorityUtils authorityUtils;
    private final PasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());
        Member member = saveOrLoadMember(attributes);

        return new DefaultOAuth2User(Collections
                .singletonList(new SimpleGrantedAuthority(authorityUtils.createAuthorities(member.getRoles()).toString())),
                attributes.getAttributes(),
                attributes.getNameAttributeKey());

    }

    private Member saveOrLoadMember(OAuthAttributes attributes) {

        String password = passwordEncoder.encode(UUID.randomUUID().toString());

        Member member = memberRepository.findByEmail(attributes.getEmail())
                .map(entity -> entity.update(attributes.getName(), attributes.getPicture(), password))
                .orElse(attributes.toEntity());

            log.info("OAuth : 회원가입 성공");

            return memberRepository.save(member);
    }
}
