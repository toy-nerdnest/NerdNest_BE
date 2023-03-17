package com.server.domain.likes.repository;

import com.server.domain.blog.entity.Blog;
import com.server.domain.likes.entity.Likes;
import com.server.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Likes, Long> {
    Optional<Likes> findByMemberAndBlog(Member member, Blog blog);
    void deleteByMemberAndBlog(Member member, Blog blog);
    Page<Likes> findByMemberAndLikeStatus(Member member, Likes.LikeStatus likeStatus, Pageable pageable);
}
