package com.server.domain.blog.dto;

import com.server.domain.category.entity.Category;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class BlogDto {
    @Getter
    public static class Post {
        private String blogTitle;
        private String blogContent;
        private String titleImageUrl;
        @NotNull(message = "categoryId를 입력해주세요")
        private long categoryId;
    }

    @Getter
    @Setter
    public static class Patch {
        @NotBlank(message = "blogId를 입력해주세요")
        private Long blogId;
        private String blogTitle;
        private String blogContent;
        private String titleImageUrl;
        @NotNull(message = "categoryId를 입력해주세요")
        private Long categoryId;
    }
}
