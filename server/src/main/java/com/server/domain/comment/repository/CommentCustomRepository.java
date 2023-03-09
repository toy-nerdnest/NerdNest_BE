package com.server.domain.comment.repository;

import com.server.domain.blog.entity.Blog;
import com.server.domain.comment.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommentCustomRepository {
    List<Comment> findCommentByBlog(Blog blog);
}
