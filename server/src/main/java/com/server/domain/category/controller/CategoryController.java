package com.server.domain.category.controller;

import com.server.domain.category.dto.CategoryResponseDto;
import com.server.domain.category.mapper.CategoryMapper;
import com.server.domain.category.dto.CategoryDto;
import com.server.domain.category.entity.Category;
import com.server.domain.category.service.CategoryService;
import com.server.domain.member.entity.Member;
import com.server.domain.member.service.MemberService;
import com.server.response.SingleResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/category")
public class CategoryController {
    private final CategoryService categoryService;
    private final CategoryMapper mapper;

    private final MemberService memberService;
    private static final long MAIN_CATEGORY_ID = 1L;

    @PostMapping({"/{member-id}"})
    public ResponseEntity<HttpStatus> postSingleCategory(@RequestBody @Valid CategoryDto.Post categoryDtoPost,
                                                         @PathVariable("member-id") @Positive Long memberId) {
        // TODO: memberId
        Member member = memberService.findMember(memberId);
        Category category = mapper.categoryDtoPostToCategory(categoryDtoPost, member);
        categoryService.makeSingleCategory(category);

        return new ResponseEntity(HttpStatus.CREATED);
    }

    @PatchMapping("/{category-id}")
    public ResponseEntity<HttpStatus> patchSingleCategory(@RequestBody CategoryDto.Patch categoryDtoPatch,
                                                          @PathVariable("category-id") @Min(value = 2) long categoryId) {
        categoryDtoPatch.setCategoryId(categoryId);
        Category category = mapper.categoryDtoPatchToCategory(categoryDtoPatch);
        categoryService.editSingleCategory(category);

        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<SingleResponseDto> getAllCategories() {
        List<Category> allCategories = categoryService.findAllCategories();
        List<CategoryResponseDto> categoryResponseDtos = mapper.categoryToCategoryResponseDto(allCategories);

        return ResponseEntity.ok(new SingleResponseDto(categoryResponseDtos));
    }

    @DeleteMapping("/{category-id}")
    public ResponseEntity<HttpStatus> deleteSingleCategory(@PathVariable("category-id") @Min(value = 2) long categoryId) {
        categoryService.deleteSingleCategory(categoryId);

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
