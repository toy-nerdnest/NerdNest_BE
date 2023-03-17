package com.server.domain.category.controller;

import com.server.domain.category.dto.CategoryResponseDto;
import com.server.domain.category.mapper.CategoryMapper;
import com.server.domain.category.dto.CategoryDto;
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
public class CategoryController {
    private final CategoryService categoryService;
    private final CategoryMapper mapper;
    private final MemberService memberService;
    @PostMapping("/category")
    public ResponseEntity<HttpStatus> postSingleCategory(@RequestBody @Valid CategoryDto.Post categoryDtoPost,
                                                         @AuthenticationPrincipal Member loginMember) {
        if (loginMember == null) {
            log.error("loginMember is null : 허용되지 않은 접근입니다.");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Member foundMember = memberService.findMember(loginMember.getMemberId());
        Category category = mapper.categoryDtoPostToCategory(categoryDtoPost, foundMember);
        categoryService.makeSingleCategory(category);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PatchMapping("/category/{category-id}")
    public ResponseEntity<HttpStatus> patchSingleCategory(@RequestBody CategoryDto.Patch categoryDtoPatch,
                                                          @PathVariable("category-id") long categoryId,
                                                          @AuthenticationPrincipal Member loginMember) {
        if (loginMember == null) {
            log.error("loginMember is null : 허용되지 않은 접근입니다.");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        categoryService.verifyCategory(categoryId, loginMember);
        categoryDtoPatch.setCategoryId(categoryId);
        Category category = mapper.categoryDtoPatchToCategory(categoryDtoPatch);
        categoryService.editSingleCategory(category);

        return new ResponseEntity<>(HttpStatus.OK);
    }


    @GetMapping("/category")
    public ResponseEntity<?> getAllCategories(@RequestParam(required = false, defaultValue = "1") int page,
                                                             @RequestParam(required = false, defaultValue = "12") int size) {
        Page<Category> categoryPageInfo = categoryService.findAllCategories(page, size);
        List<Category> categories = categoryPageInfo.getContent();
        List<CategoryResponseDto> categoryResponseDtos = mapper.categoriesToCategoryResponseDto(categories);

        return new ResponseEntity<>(new MultiResponseDto.CategoryList<>(categoryResponseDtos, categoryPageInfo), HttpStatus.OK);
    }

    @GetMapping("/category/{member-id}")
    public ResponseEntity<?> getAllCategoriesEachMember(@PathVariable("member-id") @Positive long memberId) {
        List<Category> allCategoriesEachMember = categoryService.findAllCategoriesEachMember(memberId);
        List<CategoryResponseDto> categoryResponseDtos = mapper.categoriesToCategoryResponseDto(allCategoriesEachMember);

        return ResponseEntity.ok(new SingleResponseDto.Category(categoryResponseDtos));
    }

    @DeleteMapping("/category/{category-id}")
    public ResponseEntity<HttpStatus> deleteSingleCategory(@PathVariable("category-id") long categoryId,
                                                           @AuthenticationPrincipal Member loginMember) {
        if (loginMember == null) {
            log.error("loginMember is null : 허용되지 않은 접근입니다.");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Category baseCategory = categoryService.verifyCategory(categoryId, loginMember);

        Category categoryToDelete = categoryService.findSingleCategoryById(categoryId);
        categoryToDelete.getBlogList().forEach(
                blog -> blog.setCategory(baseCategory)
        );

        categoryService.deleteSingleCategory(categoryToDelete.getCategoryId());

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
