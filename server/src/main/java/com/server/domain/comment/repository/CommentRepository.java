package com.server.domain.comment.repository;


import com.server.domain.blog.entity.Blog;
import com.server.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Long countByBlog(Blog blog);
}
