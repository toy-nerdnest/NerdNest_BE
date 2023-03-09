package com.server.domain.member.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import javax.validation.constraints.*;

@Data
public class MemberDto {

    @Data
    @Builder
    public static class Post {
        @NotBlank
        @Email
        private String email;

        @NotBlank
        @Pattern(regexp = "^[0-9a-zA-Z가-핳]{2,10}$")
        private String nickName;

        @NotBlank
        @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+])[a-zA-Z\\d!@#$%^&*()_+]{6,10}$")  // 변경필
        private String password;
    }

    @Getter
    @Builder
    public static class Patch {
        @NotBlank
        @Pattern(regexp = "^[0-9a-zA-Z가-핳]{2,10}$")
        private String nickName;

        @NotNull
        private String about;

        private Long profileImageUrl;
    }
    @Getter
    @Builder
    public static class Response {
        private String nickName;
        private String about;
        private String profileImageUrl;
    }
}
