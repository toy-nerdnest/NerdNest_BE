package com.server.domain.comment.repository;


import com.server.domain.blog.entity.Blog;
import com.server.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
