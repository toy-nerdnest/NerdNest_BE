package com.server.domain.member.dto;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public class MemberDto {

    @Getter
    @Builder
    public static class Post {
        @NotBlank
        @Email
        private String email;

        @NotBlank
        private String nickName;

        @NotBlank
        private String password;
    }

    @Getter
    @Builder
    public static class Patch {
        @NotBlank
        private String nickName;
        @NotNull
        private String about;
        @Positive
        @NotNull
        private Long ImageFileId;

    }
    @Getter
    @Builder
    public static class Response {
        private String profileImageUrl;
        private String nickName;
        private String about;
    }
}
