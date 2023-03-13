package com.server.domain.blog.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.server.domain.blog.dto.BlogDto;
import com.server.domain.blog.dto.BlogResponseDto;
import com.server.domain.blog.entity.Blog;
import com.server.domain.blog.mapper.BlogMapper;
import com.server.domain.blog.service.BlogService;
import com.server.domain.category.entity.Category;
import com.server.domain.category.service.CategoryService;
import com.server.domain.comment.dto.CommentResponseDto;

import com.server.domain.comment.service.CommentService;
import com.server.domain.member.entity.Member;
import com.server.domain.member.service.MemberService;
import com.server.response.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class BlogController {
    private final BlogService blogService;
    private final BlogMapper mapper;
    private final CategoryService categoryService;
    private final MemberService memberService;
    private final CommentService commentService;

    /* 블로그 등록 */
    @PostMapping("/blogs")
    public ResponseEntity<?> postBlog(@RequestBody @Valid BlogDto.Post blogPostDto,
                                      @AuthenticationPrincipal Member loginMember) {
        if (loginMember == null) {
            log.error("loginMember is null : 허용되지 않은 접근입니다.");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Member foundMember = memberService.findMember(loginMember.getMemberId());
        Category category = categoryService.findSingleCategoryById(blogPostDto.getCategoryId());
        Blog blog = mapper.blogPostDtoToBlog(blogPostDto, category, foundMember);
        blogService.createBlog(blog);

        // blogId 리스폰스 데이터 추가
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("blogId", blog.getBlogId());
        String blogIdToJson
                = gson.toJson(jsonObject);

        return new ResponseEntity<>(blogIdToJson, HttpStatus.CREATED);
    }

    /* 블로그 수정 */
    @PatchMapping("/blogs/edit/{blog-id}")
    public ResponseEntity<HttpStatus> patchBlog(@RequestBody @Valid BlogDto.Patch blogPatchDto,
                                                @PathVariable("blog-id") @Positive long blogId,
                                                @AuthenticationPrincipal Member loginMember) {
        if (loginMember == null) {
            log.error("loginMember is null : 허용되지 않은 접근입니다.");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        blogService.verifyOwner(blogId, loginMember);
        blogPatchDto.setBlogId(blogId);
        Category singleCategory = categoryService.findSingleCategoryById(blogPatchDto.getCategoryId());
        Blog blog = mapper.blogPatchDtoToBlog(blogPatchDto, singleCategory);
        blogService.editBlog(blog);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /* 블로그 조회 */
    @GetMapping("/blogs/edit/{blog-id}")
    public ResponseEntity<SingleResponseDto<?>> getBlog(@PathVariable("blog-id") @Positive long blogId) {
        Blog blog = blogService.findBlogById(blogId);
        BlogResponseDto blogResponseDto = mapper.blogToBlogResponseDto(blog);

        return ResponseEntity.ok(new SingleResponseDto<>(blogResponseDto));
    }

    /* 홈 화면 데이터 - 추천순, 최신순 */
    @GetMapping("/home/blogs")
    public ResponseEntity<ListResponseDto<?>> getBlogHomeData(@RequestParam(defaultValue = "newest", required = false) String tab,
                                                              @RequestParam(defaultValue = "1", required = false) int page,
                                                              @RequestParam(defaultValue = "12", required = false) int size) {
        //TODO : tab에 따른 likes 정렬기능 추가예정
        Page<Blog> blogsPageInfo = blogService.findAllBlog(switchTabToSort(tab), page, size);
        List<Blog> blogs = blogsPageInfo.getContent();
        List<BlogResponseDto.Home> blogResponseHomeDto = mapper.blogListToBlogResponseHomeDto(blogs);

        return new ResponseEntity<>(new ListResponseDto<>(blogResponseHomeDto), HttpStatus.OK);
    }

    private static String switchTabToSort(String tab) {
        String sort = "";
        switch (tab) {
            case "newest":
                sort = "blogId";
                break;
            case "likes":
                sort = "likeCount";
                break;
        }
        return sort;
    }

    /* 홈 화면 데이터 - 내추천 */
    @GetMapping("/home/blogs/mylikes")
    public ResponseEntity getBlogHomeDataByMyLikes(@RequestParam(defaultValue = "1", required = false) int page,
                                                   @RequestParam(defaultValue = "12", required = false) int size,
                                                   @AuthenticationPrincipal Member loginMember) {

        if (loginMember == null) {
            log.error("loginMember is null : 허용되지 않은 접근입니다.");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Member member = memberService.findMember(loginMember.getMemberId());

        Page<Blog> blogPage = blogService.findBlogsByMemberWithLike(member, page, size);

        List<Blog> blogs = blogPage.getContent();
        List<BlogResponseDto.Home> blogResponseHomeDto = mapper.blogListToBlogResponseHomeDto(blogs);

        return new ResponseEntity<>(new ListResponseDto<>(blogResponseHomeDto), HttpStatus.OK);
    }

    /* 멤버 개인 블로그 데이터 */
    @GetMapping("/blogs/member/{nickname}")
    public ResponseEntity getPersonalBlogData(@PathVariable("nickname") String nickname,
                                              @RequestParam(required = false) Long categoryId,
                                              @RequestParam(defaultValue = "1", required = false) int page,
                                              @RequestParam(defaultValue = "8", required = false) int size) {
        log.info("categoryId = {}", categoryId);
        Member member = memberService.findMember(nickname);

        Category singleCategoryById = categoryService.findSingleCategoryById(categoryId);

        // categoryId 없으면 멤버가 작성한 모든 블로그 리턴
        if (categoryId == null || singleCategoryById.getCategoryName().equals("전체")) {
            Page<Blog> blogsByMemberNickname = blogService.findBlogsByMemberNickname(nickname, page, size);
            return getResponseEntity(page, blogsByMemberNickname);
        }

        // categoryId 있으면 해당 category에 대한 블로그 내역 리턴
        Page<Blog> blogsByCategoryId = blogService.findBlogsByCategoryId(categoryId, page, size);
        return getResponseEntity(page, blogsByCategoryId);
    }

    // 무한스크롤에 대한 isNextPage true false 여부 리스폰스 추가
    private ResponseEntity getResponseEntity(@RequestParam(defaultValue = "1", required = false) int page, Page<Blog> blogsByCategoryId) {
        int totalPages = blogsByCategoryId.getTotalPages();
        boolean isNextPage = blogService.judgeNextPage(page, totalPages);

        List<Blog> blogs = blogsByCategoryId.getContent();
        List<BlogResponseDto.Member> blogResponseMemberDtos = mapper.blogListToBlogResponseMemberDto(blogs);

        return new ResponseEntity<>(new ScrollResponseDto<>(isNextPage, blogResponseMemberDtos), HttpStatus.OK);
    }

    /* 블로그 상세 데이터 */
    @GetMapping("/blogs/{blog-id}")
    public ResponseEntity<SingleResponseDto<?>> getBlogById(@PathVariable("blog-id") @Positive long blogId) {

        Blog blog = blogService.findBlogById(blogId);
        List<CommentResponseDto> layeredComments = commentService.getLayeredComments(blog);
        BlogResponseDto.Detail blogResponseDtoDetail = mapper.blogListToBlogDetailResponseDtoWithComment(blog, layeredComments);

        return ResponseEntity.ok(new SingleResponseDto<>(blogResponseDtoDetail));
    }

    /* 블로그 상세 데이터 - 삭제 */
    @DeleteMapping("/blogs/{blog-id}")
    public ResponseEntity<HttpStatus> deleteBlog(@PathVariable("blog-id") @Positive long blogId,
                                                 @AuthenticationPrincipal Member loginMember) {
        if (loginMember == null) {
            log.error("loginMember is null : 허용되지 않은 접근입니다.");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        blogService.verifyOwner(blogId, loginMember);
        blogService.deleteBlog(blogId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /* 검색 페이지 */
    @GetMapping("/search")
    public ResponseEntity searchBlog(@RequestParam(required = false) String keyword,
                                     @RequestParam(defaultValue = "1") int page,
                                     @RequestParam(defaultValue = "12") int size) {
        Page<Blog> pageBlogs = blogService.searchBlog(keyword, page, size);
        List<Blog> blogs = pageBlogs.getContent();
        List<BlogResponseDto.Home> responses = mapper.blogListToBlogResponseHomeDto(blogs);

        return new ResponseEntity<>(new MultiResponseDto.BlogList<>(responses, pageBlogs), HttpStatus.OK);
    }
}
