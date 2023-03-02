package com.server.domain.blog.controller;

import com.server.domain.blog.dto.BlogDto;
import com.server.domain.blog.dto.BlogResponseDto;
import com.server.domain.blog.entity.Blog;
import com.server.domain.blog.mapper.BlogMapper;
import com.server.domain.blog.service.BlogService;
import com.server.domain.category.entity.Category;
import com.server.domain.category.service.CategoryService;
import com.server.response.SingleResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/blogs")
public class BlogController {
    private final BlogService blogService;
    private final BlogMapper mapper;
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity postBlog(@RequestBody @Valid BlogDto.Post blogPostDto) {
        //TODO: Member & Comment 추가예정
        Category singleCategory = categoryService.findSingleCategory(blogPostDto.getCategoryId());
        Blog blog = mapper.blogPostDtoToBlog(blogPostDto, singleCategory);
        blogService.createBlog(blog);

        return new ResponseEntity(HttpStatus.CREATED);
    }

    @PatchMapping("/edit/{blog-id}")
    public ResponseEntity patchBlog(@RequestBody @Valid BlogDto.Patch blogPatchDto,
                                    @PathVariable("blog-id") @Positive long blogId) {
        blogPatchDto.setBlogId(blogId);
        Category singleCategory = categoryService.findSingleCategory(blogPatchDto.getCategoryId());
        Blog blog = mapper.blogPatchDtoToBlog(blogPatchDto, singleCategory);
        blogService.editBlog(blog);

        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/edit/{blog-id}")
    public ResponseEntity getPost(@PathVariable("blog-id") @Positive long blogId) {
        Blog blog = blogService.findBlog(blogId);
        BlogResponseDto blogResponseDto = mapper.blogToBlogResponseDto(blog);

        return ResponseEntity.ok(new SingleResponseDto<>(blogResponseDto));
    }

    @DeleteMapping("{blog-id}")
    public ResponseEntity deletePost(@PathVariable("blog-id") @Positive long blogId) {
        blogService.deleteBlog(blogId);

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
