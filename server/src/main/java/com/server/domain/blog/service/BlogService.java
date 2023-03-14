package com.server.domain.blog.service;

import com.server.domain.blog.entity.Blog;
import com.server.domain.blog.repository.BlogRepository;
import com.server.domain.category.entity.Category;
import com.server.domain.category.service.CategoryService;
import com.server.domain.imageFile.service.ImageFileService;
import com.server.domain.likes.entity.Likes;
import com.server.domain.likes.repository.LikeRepository;
import com.server.domain.member.entity.Member;
import com.server.domain.member.service.MemberService;
import com.server.exception.BusinessLogicException;
import com.server.exception.ExceptionCode;
import com.server.utils.CustomBeanUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class BlogService {
    private final BlogRepository blogRepository;
    private final CustomBeanUtils beanUtils;
    private final CategoryService categoryService;
    private final MemberService memberService;
    private final ImageFileService imageFileService;
    private final LikeRepository likeRepository;

    public void createBlog(Blog blog) {

        if(blog.getTitleImageUrl().isEmpty()) {
            blog.setTitleImageUrl(imageFileService.getDefaultTitleImgUrl());
            log.info("Blog Title Image : 기본 이미지 저장 완료");
        }
        blogRepository.save(blog);
    }

    public void editBlog(Blog blog) {
        Blog dbBlog = findBlogById(blog.getBlogId());
        beanUtils.copyNonNullProperties(blog, dbBlog);

        blogRepository.save(dbBlog);
    }

    public void plusBlogCount(Blog blog) {
        int commentCount = blog.getCommentCount();
        blog.setCommentCount(commentCount + 1);
        blogRepository.save(blog);
    }

    public void minusBlogCount(Blog blog) {
        int commentCount = blog.getCommentCount();

        if (commentCount > 0) {
            blog.setCommentCount(commentCount - 1);
            blogRepository.save(blog);
        }
    }

    public Blog findBlogById(Long blogId) {
        return verifyBlogId(blogId);
    }

    public Page<Blog> findAllBlog(String sort, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(sort).descending());

        return blogRepository.findAll(pageable);
    }

    public Page<Blog> findBlogsByCategoryName(String categoryName, int page, int size) {
        Category singleCategory = categoryService.findSingleCategoryByName(categoryName);
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("blogId").descending());
        Page<Blog> blogs = blogRepository.findAllByCategory(singleCategory, pageable);

        return blogs;
    }

    public Page<Blog> findBlogsByCategoryId(long categoryId, int page, int size) {
        Category singleCategory = categoryService.findSingleCategoryById(categoryId);
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("blogId").descending());
        Page<Blog> blogs = blogRepository.findAllByCategory(singleCategory, pageable);

        return blogs;
    }

    public Page<Blog> findBlogsByMemberWithLike(Member member, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("blogId").descending());

        List<Likes> likes = likeRepository.findByMemberAndLikeStatus(member, com.server.domain.likes.entity.Likes.LikeStatus.LIKE);

        List<Blog> blogList = likes.stream()
                        .map(like -> blogRepository.findById(like.getBlog().getBlogId()))
                        .map(Optional::get)
                        .collect(Collectors.toList());

        Page<Blog> blogs = new PageImpl<>(blogList, pageable, blogList.size());

        return blogs;
    }

    public Page<Blog> findBlogsByMemberNickname(String nickname, int page, int size) {
        Member member = memberService.findMember(nickname);
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("blogId").descending());
        Page<Blog> blogs = blogRepository.findAllByMember(member, pageable);

        return blogs;
    }

    public void deleteBlog(long blogId) {
        Blog verifiedBlog = verifyBlogId(blogId);
        blogRepository.delete(verifiedBlog);
    }

    public Page<Blog> searchBlog(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page-1, size, Sort.by("blogId").descending());
        log.info("search keyword : {}", keyword);
        Page<Blog> blogs = blogRepository.findByBlogTitleContaining(keyword, pageable);

        return blogs;
    }

    private Blog verifyBlogId(long blogId) {
        return blogRepository.findById(blogId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.BLOG_NOT_FOUND));
    }

    public void verifyOwner(long blogId, Member loginMember) {
        Long loginMemberId = loginMember.getMemberId();
        Long ownerId = findBlogById(blogId).getMember().getMemberId();
        if (loginMemberId != ownerId) {
            throw new BusinessLogicException(ExceptionCode.MEMBER_NOT_AUTHORIZED);
        }
    }

    public boolean judgeNextPage(int curPage, Page<Blog> blogPage) {
        int totalPages = blogPage.getTotalPages();
        if (curPage > totalPages) {
            throw new BusinessLogicException(ExceptionCode.INVALID_PAGE);
        }

        if (curPage == totalPages) {
            return false;
        }
        return true;
    }

}
