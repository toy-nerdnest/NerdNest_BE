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
    public ResponseEntity<MultiResponseDto> getAllCategories(@RequestParam(required = false, defaultValue = "1") int page,
                                                              @RequestParam(required = false, defaultValue = "12") int size) {
        Page<Category> categoryPageInfo = categoryService.findAllCategories(page, size);
        List<Category> categories = categoryPageInfo.getContent();
        List<CategoryResponseDto> categoryResponseDtos = mapper.categoriesToCategoryResponseDto(categories);

        return new ResponseEntity(new MultiResponseDto.CategoryList<>(categoryResponseDtos, categoryPageInfo), HttpStatus.OK);
    }

    @GetMapping("/{member-id}")
    public ResponseEntity<SingleResponseDto.Category> getAllCategoriesEachMember(@PathVariable("member-id") @Positive long memberId) {
        List<Category> allCategoriesEachMember = categoryService.findAllCategoriesEachMember(memberId);
        List<CategoryResponseDto> categoryResponseDtos = mapper.categoriesToCategoryResponseDto(allCategoriesEachMember);

        return ResponseEntity.ok(new SingleResponseDto.Category(categoryResponseDtos));
    }

    @DeleteMapping("/{category-id}")
    public ResponseEntity<HttpStatus> deleteSingleCategory(@PathVariable("category-id") @Min(value = 2) long categoryId) {
        categoryService.deleteSingleCategory(categoryId);

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
