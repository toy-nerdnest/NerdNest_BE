package com.server.security.oauth.service;

import com.server.domain.category.entity.Category;
import com.server.domain.category.repository.CategoryRepository;
import com.server.domain.member.entity.Member;
import com.server.domain.member.repository.MemberRepository;
import com.server.exception.BusinessLogicException;
import com.server.exception.ExceptionCode;
import com.server.security.oauth.utils.OAuthAttributes;
import com.server.security.utils.MemberAuthorityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.inject.Inject;
import java.util.*;

@Service
@Slf4j
public class MemberOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    // 사용자의 정보를 기반으로 가입 및 정보 저장 기능을 한다.
    private final MemberRepository memberRepository;
    private final MemberAuthorityUtils authorityUtils;
    private final PasswordEncoder passwordEncoder;
    private final CategoryRepository categoryRepository;
    public MemberOAuth2UserService(MemberRepository memberRepository,
                                   MemberAuthorityUtils authorityUtils,
                                   @Lazy PasswordEncoder passwordEncoder,
                                   CategoryRepository categoryRepository) {
        this.memberRepository = memberRepository;
        this.authorityUtils = authorityUtils;
        this.passwordEncoder = passwordEncoder;
        this.categoryRepository = categoryRepository;
    }

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

        Optional<Member> optionalMember = memberRepository.findByEmail(attributes.getEmail());

        if(optionalMember.isPresent()) {
           Member member = optionalMember
                    .get()
                    .update(attributes.getName(), attributes.getPicture(), password);

           return memberRepository.save(member);
        } else {
            Member member = attributes.toEntity();
            member.setPassword(password);

            Member saveMember = memberRepository.save(member);

            List<Category> categoryList = new ArrayList<>();
            Category category = Category.builder()
                    .categoryName("전체")
                    .member(saveMember)
                    .build();

            categoryList.add(category);
            categoryRepository.save(category);

            saveMember.setCategories(categoryList);
            return saveMember;
        }
    }
}
