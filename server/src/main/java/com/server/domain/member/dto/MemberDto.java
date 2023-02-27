package com.server.domain.member.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class MemberDto {

    public static class Post {
        @NotBlank
        @Email
        private String email;

        @NotBlank
        private String nickName;

        @NotNull
        private String password;
    }

    public static class Patch {
        // 이미지 URL
        @NotBlank
        private String nickName;
        @NotNull
        private String about;
    }

    public static class Response {
        // 이미지 URL
        private String nickName;
        private String about;
    }
}
