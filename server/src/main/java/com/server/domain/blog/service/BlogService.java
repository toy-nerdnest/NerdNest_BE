package com.server.domain.blog.service;

import com.server.domain.blog.dto.BlogResponseDto;
import com.server.domain.blog.entity.Blog;
import com.server.domain.blog.repository.BlogRepository;
import com.server.exception.BusinessLogicException;
import com.server.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BlogService {
    private final BlogRepository blogRepository;

    public void createBlog(Blog blog) {
        Blog save = blogRepository.save(blog);
    }

    public void deleteBlog(long blogId) {
        Blog verifiedBlog = verifyBlogId(blogId);
        blogRepository.delete(verifiedBlog);
    }

    public Blog verifyBlogId(long blogId) {
        return blogRepository.findById(blogId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.BLOG_NOT_FOUND));
    }
}
