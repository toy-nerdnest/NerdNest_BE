package com.server.domain.blog.dto;

import com.server.domain.category.entity.Category;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

public class BlogDto {

    @Getter
    public static class Post {

        private String titleImageUrl;

        private String blogTitle;

        private String blogContent;

        @Setter
        private int categoryId;

    }



}
