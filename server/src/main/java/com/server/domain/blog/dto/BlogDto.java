package com.server.domain.blog.dto;

import com.server.domain.category.entity.Category;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

public class BlogDto {
    @Getter
    @Setter
    public static class Post {
        @NotBlank(message = "제목을 필수로 입력해주세요")
        private String blogTitle;
        private String blogContent;
        private String titleImageUrl;
        @NotNull(message = "categoryId를 입력해주세요")
        @PositiveOrZero
        private Long categoryId;
    }

    @Getter
    @Setter
    public static class Patch {
        private Long blogId;
        @NotBlank(message = "제목을 필수로 입력해주세요")
        private String blogTitle;
        private String blogContent;
        private String titleImageUrl;
        @NotNull(message = "categoryId를 입력해주세요")
        private Long categoryId;
    }
}
