package com.server.domain.category.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class CategoryDto {
    @Getter
    public static class Post {
        private String categoryName;
    }

    @Getter
    public static class Patch {
        @Setter
        private Long categoryId;
        private String categoryName;

    }
}
