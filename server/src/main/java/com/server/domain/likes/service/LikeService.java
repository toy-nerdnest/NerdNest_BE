package com.server.domain.likes.service;


import com.server.domain.blog.entity.Blog;
import com.server.domain.blog.service.BlogService;
import com.server.domain.likes.entity.Likes;
import com.server.domain.likes.repository.LikeRepository;
import com.server.domain.member.entity.Member;
import com.server.domain.member.service.MemberService;
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
    private final MemberService memberService;

    public Likes likeBlogs(Member loginMember, long blogId) {

        Member member = memberService.findMember(loginMember.getMemberId());
        Blog blog = blogService.findBlogById(blogId);

        // 블로그에 좋아요한 흔적 찾기 -> 없다면 좋아요 누르기, 있다면 status 확인
        Optional<Likes> optionalLike = likeRepository.findByMemberAndBlog(member, blog);

        Likes likes = null;

        if(optionalLike.isPresent()) {
            likes = optionalLike.get();
            return setStatus(likes);
        } else {
            likes = new Likes(member, blog);
            plusLikeCount(likes);
            return likeRepository.save(likes);
        }

    }

    private Likes setStatus(Likes likes) {

        if(likes.getLikeStatus() == com.server.domain.likes.entity.Likes.LikeStatus.LIKE) {
            likes.setLikeStatus(com.server.domain.likes.entity.Likes.LikeStatus.CANCEL);
            minusLikeCount(likes);
        } else {
            likes.setLikeStatus(com.server.domain.likes.entity.Likes.LikeStatus.LIKE);
            plusLikeCount(likes);
        }

        return likeRepository.save(likes);
    }

    private void plusLikeCount(Likes likes) {
        Blog blog = likes.getBlog();
        blog.setLikeCount(blog.getLikeCount()+ 1);

        likes.setBlog(blog);
    }
    private void minusLikeCount(Likes likes) {
        Blog blog = likes.getBlog();
        blog.setLikeCount(blog.getLikeCount()-1);

        likes.setBlog(blog);
    }

}
