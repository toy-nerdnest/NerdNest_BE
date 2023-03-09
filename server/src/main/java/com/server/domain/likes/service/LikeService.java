package com.server.domain.likes.service;


import com.server.domain.blog.entity.Blog;
import com.server.domain.blog.service.BlogService;
import com.server.domain.likes.entity.Like;
import com.server.domain.likes.repository.LikeRepository;
import com.server.domain.member.entity.Member;
import com.server.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
@RequiredArgsConstructor
@Service
public class LikeService {

    private final LikeRepository likeRepository;
    private final BlogService blogService;
    private final MemberService memberService;

    public Like likeBlogs(Member loginMember, long blogId) {

        Member member = memberService.findMember(loginMember.getMemberId());
        Blog blog = blogService.findBlogById(blogId);

        // 블로그에 좋아요한 흔적 찾기 -> 없다면 좋아요 누르기, 있다면 status 확인
        Optional<Like> optionalLike = likeRepository.findByMemberAndBlog(member, blog);

        Like like = null;

        if(optionalLike.isPresent()) {
            like = optionalLike.get();
            return setStatus(like);
        } else {
            like = new Like(member, blog);
            plusLikeCount(like);
            return likeRepository.save(like);
        }

    }

    private Like setStatus(Like like) {

        if(like.getLikeStatus() == Like.LikeStatus.LIKE) {
            like.setLikeStatus(Like.LikeStatus.CANCEL);
            minusLikeCount(like);
        } else {
            like.setLikeStatus(Like.LikeStatus.LIKE);
            plusLikeCount(like);
        }

        return likeRepository.save(like);
    }

    private void plusLikeCount(Like like) {
        Blog blog = like.getBlog();
        blog.setLikeCount(blog.getLikeCount()+ 1);

        like.setBlog(blog);
    }
    private void minusLikeCount(Like like) {
        Blog blog = like.getBlog();
        blog.setLikeCount(blog.getLikeCount()-1);

        like.setBlog(blog);
    }

}
