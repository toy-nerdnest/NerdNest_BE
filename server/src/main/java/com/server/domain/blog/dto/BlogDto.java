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
        private String titleImageUrl;
        private String blogTitle;
        private String blogContent;
        private long categoryId;
    }

    @Getter
    @Setter
    public static class Patch {
        private Long blogId;
        private String titleImageUrl;
        private String blogTitle;
        private String blogContent;
        private String categoryName;
    }
}
