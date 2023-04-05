package com.server.domain.blog.repository;

import com.server.domain.blog.entity.Blog;

import java.util.List;

public interface BlogCustomRepository {
    List<Blog> findBlogByMemberIdAndYearIn(Long memberId, int year);
}
