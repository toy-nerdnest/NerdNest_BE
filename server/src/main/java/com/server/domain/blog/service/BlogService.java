package com.server.domain.blog.service;

import com.server.domain.blog.dto.BlogResponseDto;
import com.server.domain.blog.entity.Blog;
import com.server.domain.blog.repository.BlogRepository;
import com.server.exception.BusinessLogicException;
import com.server.exception.ExceptionCode;
import com.server.utils.CustomBeanUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BlogService {
    private final BlogRepository blogRepository;
    private final CustomBeanUtils beanUtils;

    public void createBlog(Blog blog) {
        blogRepository.save(blog);
    }

    public void editBlog(Blog blog) {
        Blog dbBlog = findBlog(blog.getBlogId());
        beanUtils.copyNonNullProperties(blog, dbBlog);

        blogRepository.save(dbBlog);
    }

    public Blog findBlog(long blogId) {
        return verifyBlogId(blogId);
    }

    public void deleteBlog(long blogId) {
        Blog verifiedBlog = verifyBlogId(blogId);
        blogRepository.delete(verifiedBlog);
    }

    private Blog verifyBlogId(long blogId) {
        return blogRepository.findById(blogId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.BLOG_NOT_FOUND));
    }

}
