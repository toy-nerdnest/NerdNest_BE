package com.server.domain.likes.service;


import com.server.domain.blog.entity.Blog;
import com.server.domain.blog.service.BlogService;
import com.server.domain.likes.entity.Like;
import com.server.domain.likes.repository.LikeRepository;
import com.server.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
@RequiredArgsConstructor
@Service
public class LikeService {

    private final LikeRepository likeRepository;
    private final BlogService blogService;

    public boolean findLike(Member member, Blog blog) {
        Optional<Like> byMemberAndBlog = likeRepository.findByMemberAndBlog(member, blog);
        if (byMemberAndBlog.isPresent()) {
            return true;
        }

        return false;
    }

    public void addLike(Member member, Blog blog) {
        likeRepository.save(new Like(member, blog));
    }

    public void deleteLike(Member member, Blog blog) {
        likeRepository.deleteByMemberAndBlog(member, blog);
    }

}
