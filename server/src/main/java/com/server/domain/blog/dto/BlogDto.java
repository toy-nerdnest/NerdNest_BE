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
        private Long categoryId;
    }

    @Getter
    @Setter
    public static class Patch {
        private Long blogId;
        private String blogTitle;
        private String blogContent;
        private String titleImageUrl;
        private Long categoryId;
    }
}
