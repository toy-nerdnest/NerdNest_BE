package com.server.domain.blog.repository;

import com.server.domain.blog.entity.Blog;
import com.server.domain.category.entity.Category;
import com.server.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface BlogRepository extends JpaRepository<Blog, Long> {

    Page<Blog> findAllByCategory(Category category, Pageable pageable);
    Page<Blog> findAllByMember(Member member, Pageable pageable);
    List<Blog> findAllByMemberAndLikeStatusIsTrue(Member member);
//    List<Blog> findAllByMemberAndBlogS(Member member);

    Page<Blog> findByBlogContentContaining(String keyword, Pageable pageable);
}
