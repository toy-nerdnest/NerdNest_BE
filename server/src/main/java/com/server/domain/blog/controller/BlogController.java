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
import com.server.domain.likes.dto.MyLikeBlogResponseDto;
import com.server.domain.likes.entity.Likes;
import com.server.domain.likes.mapper.LikeMapper;
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
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class BlogController {
    private final BlogService blogService;
    private final BlogMapper mapper;
    private final LikeMapper likeMapper;
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

        if (blogPostDto.getCategoryId() == 0) {
            Long categoryId = foundMember.getCategories().get(0).getCategoryId();
            blogPostDto.setCategoryId(categoryId);
        }

        Category category = categoryService.findSingleCategoryById(blogPostDto.getCategoryId());
        Blog blog = mapper.blogPostDtoToBlog(blogPostDto, category, foundMember);
        blogService.createBlog(blog);
        String responseBlogId = makeBlogIdToJson(blog);

        return new ResponseEntity<>(responseBlogId, HttpStatus.CREATED);
    }

    private static String makeBlogIdToJson(Blog blog) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("blogId", blog.getBlogId());
        String blogIdToJson
                = gson.toJson(jsonObject);
        return blogIdToJson;
    }

    /* 블로그 수정 */
    @PatchMapping("/blogs/edit/{blog-id}")
    public ResponseEntity<HttpStatus> patchBlog(@RequestBody @Valid BlogDto.Patch blogPatchDto,
                                                @PathVariable("blog-id") @Positive Long blogId,
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
        Page<Blog> blogsPageInfo = blogService.findAllBlog(switchTabToSort(tab), page, size);

        return getHomeResponseEntity(page, blogsPageInfo);
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

        Page<Likes> likesPage = blogService.findBlogsByMemberWithLike(member, page, size);
        boolean isNextPage = blogService.judgeNextPage(page, likesPage);

        List<Likes> likes = likesPage.getContent();
        List<MyLikeBlogResponseDto> response = likeMapper.likesToMyLikeBlogResponseDtos(likes);

        return new ResponseEntity<>(new ScrollResponseDto<>(isNextPage, response), HttpStatus.OK);
    }

    private ResponseEntity getHomeResponseEntity(@RequestParam(defaultValue = "1", required = false) int page, Page<Blog> blogPage) {
        boolean isNextPage = blogService.judgeNextPage(page, blogPage);
        List<Blog> blogs = blogPage.getContent();
        List<BlogResponseDto.Home> blogResponseHomeDto = mapper.blogListToBlogResponseHomeDto(blogs);

        return new ResponseEntity<>(new ScrollResponseDto<>(isNextPage, blogResponseHomeDto), HttpStatus.OK);
    }

    /* 멤버 개인 블로그 데이터 */
    @GetMapping("/blogs/member/{nickname}")
    public ResponseEntity getPersonalBlogData(@PathVariable("nickname") String nickname,
                                              @RequestParam(defaultValue = "0", required = false) Long categoryId,
                                              @RequestParam(defaultValue = "1", required = false) int page,
                                              @RequestParam(defaultValue = "8", required = false) int size) {
        log.info("categoryId = {}", categoryId);
        Member member = memberService.findMember(nickname);

        // categoryId가 0 이면 작성자 블로그 전체를 리턴
        if (categoryId == 0) {
            Page<Blog> allPageBlog
                    = blogService.findBlogsByMemberNickname(nickname, page, size);

            return getResponseEntity(page, allPageBlog);
        }

        // categoryId 있으면 해당 category에 대한 블로그 내역 리턴
        Page<Blog> blogsByCategoryId
                = blogService.findBlogsByCategoryId(categoryId, page, size);

        return getResponseEntity(page, blogsByCategoryId);
    }

    // 무한스크롤에 대한 isNextPage true false 여부 리스폰스 추가
    private ResponseEntity getResponseEntity(@RequestParam(defaultValue = "1", required = false) int page, Page<Blog> blogPages) {
        boolean isNextPage = blogService.judgeNextPage(page, blogPages);

        List<Blog> blogs = blogPages.getContent();
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
                                     @RequestParam(defaultValue = "1", required = false) int page,
                                     @RequestParam(defaultValue = "12", required = false) int size) {
        Page<Blog> pageBlogs = blogService.searchBlog(keyword, page, size);
        long totalElements = pageBlogs.getTotalElements();
        boolean isNextPage = blogService.judgeNextPage(page, pageBlogs);
        List<Blog> blogs = pageBlogs.getContent();
        List<BlogResponseDto.Home> responses = mapper.blogListToBlogResponseHomeDto(blogs);

        return new ResponseEntity<>(new SearchResponseDto<>(isNextPage, responses, totalElements), HttpStatus.OK);
    }

    /* 블로그 기록 데이터 - 레코드 */
    @GetMapping("/records/{member-id}")
    public ResponseEntity<?> getBlogRecordData(@PathVariable("member-id") @Positive Long memberId,
                                               @RequestParam int year) {
        memberService.findMember(memberId);
        List<Blog> allBlogByYear = blogService.findAllBlogByMemberAndYearIn(memberId, year);
        List<BlogResponseDto.Record> records = mapper.blogListToBlogResponseRecordDto(allBlogByYear);

        return new ResponseEntity<>(new ListResponseDto.BlogRecord<>(records), HttpStatus.OK);
    }
}