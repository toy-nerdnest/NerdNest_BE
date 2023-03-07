package com.server.response;

import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
public class MultiResponseDto<T> {
    private List<T> data;
    private PageInfo pageInfo;

    public MultiResponseDto(List<T> data, Page page) {
        this.data = data;
        this.pageInfo = new PageInfo(page.getNumber() + 1, page.getSize(), page.getTotalElements(), page.getTotalPages());
    }
    @Getter
    public static class BlogList<T> {
        private List<T> blogList;
        private PageInfo pageInfo;

        public BlogList(List<T> data, Page page) {
            this.blogList = data;
            this.pageInfo = new PageInfo(page.getNumber() + 1, page.getSize(), page.getTotalElements(), page.getTotalPages());
        }
    }

    @Getter
    public static class CategoryList<T> {
        private List<T> categoryList;
        private PageInfo pageInfo;

        public CategoryList(List<T> data, Page page) {
            this.categoryList = data;
            this.pageInfo = new PageInfo(page.getNumber() + 1, page.getSize(), page.getTotalElements(), page.getTotalPages());
        }
    }

}
