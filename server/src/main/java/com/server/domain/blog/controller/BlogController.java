package com.server.domain.blog.controller;

import com.server.domain.blog.dto.BlogDto;
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

@RestController
@RequiredArgsConstructor
@RequestMapping
@Validated
@Slf4j
public class BlogController {

    private final BlogService blogService;
    private final BlogMapper mapper;

    @PostMapping("/blogs")
    public ResponseEntity postBlog(@RequestBody @Valid BlogDto.Post blogPostDto,
                                   @RequestParam(defaultValue = "1", required = false) int categoryId) {
        //TODO: Category & Member 추가예정
        blogPostDto.setCategoryId(categoryId);
        Blog blog = mapper.blogPostDtoToBlog(blogPostDto);
        blogService.createBlog(blog);

        return new ResponseEntity(HttpStatus.CREATED);
    }

    @GetMapping("/blogs/{blog-id}")
    public ResponseEntity getPost(@PathVariable("blog-id") long blogId) {
        blogService.deleteBlog(blogId);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/blogs")
    public ResponseEntity getPosts(@RequestParam(defaultValue = "likes", required = false) String tab) {

        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("blogs/{blog-id}")
    public ResponseEntity deletePost(@PathVariable("blog-id") long blogId) {
        blogService.deleteBlog(blogId);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
