package com.server.domain.blog.controller;

import com.server.domain.blog.dto.BlogDto;
import com.server.domain.blog.dto.BlogResponseDto;
import com.server.domain.blog.entity.Blog;
import com.server.domain.blog.mapper.BlogMapper;
import com.server.domain.blog.service.BlogService;
import com.server.domain.category.entity.Category;
import com.server.domain.category.service.CategoryService;
import com.server.domain.member.entity.Member;
import com.server.domain.member.service.MemberService;
import com.server.response.MultiResponseDto;
import com.server.response.SingleResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/blogs")
public class BlogController {
    private final BlogService blogService;
    private final BlogMapper mapper;
    private final CategoryService categoryService;
    private final MemberService memberService;

    @PostMapping("{member-id}")
    public ResponseEntity postBlog(@RequestBody @Valid BlogDto.Post blogPostDto,
                                   @PathVariable(value = "member-id", required = false) long memberId) {
        //TODO: Member & Comment 추가예정
        Member member = memberService.findMember(memberId);
        Category category = categoryService.findSingleCategoryByName(blogPostDto.getCategoryName());
        Blog blog = mapper.blogPostDtoToBlog(blogPostDto, category, member);
        blogService.createBlog(blog);

        return new ResponseEntity(HttpStatus.CREATED);
    }

    @PatchMapping("/edit/{blog-id}")
    public ResponseEntity patchBlog(@RequestBody @Valid BlogDto.Patch blogPatchDto,
                                    @PathVariable("blog-id") @Positive long blogId) {
        blogPatchDto.setBlogId(blogId);
        Category singleCategory = categoryService.findSingleCategoryByName(blogPatchDto.getCategoryName());
        Blog blog = mapper.blogPatchDtoToBlog(blogPatchDto, singleCategory);
        blogService.editBlog(blog);

        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/{blog-id}")
    public ResponseEntity getBlogById(@PathVariable("blog-id") @Positive long blogId) {
        //TODO: Comment 추가 필요
        Blog blog = blogService.findBlog(blogId);
        BlogResponseDto blogResponseDto = mapper.blogToBlogResponseDto(blog);

        return ResponseEntity.ok(new SingleResponseDto<>(blogResponseDto));
    }

    @GetMapping("{category-name}")
    public ResponseEntity getBlogsByCategoryName(@PathVariable("category-name") String categoryName,
                                                 @RequestParam(required = false, defaultValue = "1") int page,
                                                 @RequestParam(required = false, defaultValue = "12") int size) {
        Page<Blog> blogsPageInfo = blogService.findBlogsByCategoryName(categoryName, page, size);
        List<Blog> blogs = blogsPageInfo.getContent();
        List<BlogResponseDto.WithCategory> blogResponseDtoWithCategory = mapper.blogListToBlogResponseDtoWithCategory(blogs);

        return new ResponseEntity(new MultiResponseDto<>(blogResponseDtoWithCategory, blogsPageInfo), HttpStatus.OK);
    }

    @GetMapping("{nickname}")
    public ResponseEntity getBlogsByNickname(@PathVariable("nickname") String nickname,
                                           @RequestParam(required = false, defaultValue = "1") int page,
                                           @RequestParam(required = false, defaultValue = "12") int size) {

        Page<Blog> blogsPageInfo = blogService.findBlogsByMemberNickname(nickname, page, size);
        List<Blog> blogs = blogsPageInfo.getContent();
        List<BlogResponseDto.WithCategory> blogResponseDtoWithCategory = mapper.blogListToBlogResponseDtoWithCategory(blogs);

        return new ResponseEntity(new MultiResponseDto<>(blogResponseDtoWithCategory, blogsPageInfo), HttpStatus.OK);
    }

    @GetMapping("/edit/{blog-id}")
    public ResponseEntity getBlog(@PathVariable("blog-id") @Positive long blogId) {
        Blog blog = blogService.findBlog(blogId);
        BlogResponseDto blogResponseDto = mapper.blogToBlogResponseDto(blog);

        return ResponseEntity.ok(new SingleResponseDto<>(blogResponseDto));
    }

    @DeleteMapping("{blog-id}")
    public ResponseEntity deleteBlog(@PathVariable("blog-id") @Positive long blogId) {
        blogService.deleteBlog(blogId);

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
