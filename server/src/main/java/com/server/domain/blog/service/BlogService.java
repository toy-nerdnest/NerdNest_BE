package com.server.domain.blog.service;

import com.server.domain.blog.entity.Blog;
import com.server.domain.blog.repository.BlogRepository;
import com.server.domain.category.entity.Category;
import com.server.domain.category.service.CategoryService;
import com.server.domain.imageFile.service.ImageFileService;
import com.server.domain.member.entity.Member;
import com.server.domain.member.service.MemberService;
import com.server.exception.BusinessLogicException;
import com.server.exception.ExceptionCode;
import com.server.utils.CustomBeanUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BlogService {
    private final BlogRepository blogRepository;
    private final CustomBeanUtils beanUtils;
    private final CategoryService categoryService;
    private final MemberService memberService;
    private final ImageFileService imageFileService;

    public void createBlog(Blog blog) {

        if(blog.getTitleImageUrl() == null) {
            blog.setTitleImageUrl(imageFileService.getDefaultTitleImgUrl());
        }
        blogRepository.save(blog);
    }

    public void editBlog(Blog blog) {
        Blog dbBlog = findBlogById(blog.getBlogId());
        beanUtils.copyNonNullProperties(blog, dbBlog);

        blogRepository.save(dbBlog);
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
}
