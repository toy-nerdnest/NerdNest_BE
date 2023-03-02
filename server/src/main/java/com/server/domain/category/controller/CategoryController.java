package com.server.domain.category.controller;

import com.server.domain.category.dto.CategoryResponseDto;
import com.server.domain.category.mapper.CategoryMapper;
import com.server.domain.category.dto.CategoryDto;
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
    private static final long MAIN_CATEGORY_ID = 1L;

    @PostMapping()
    public ResponseEntity<HttpStatus> postSingleCategory(@RequestBody @Valid CategoryDto.Post categoryDtoPost) {
        Category category = mapper.categoryDtoPostToCategory(categoryDtoPost);
        categoryService.makeSingleCategory(category);

        return new ResponseEntity(HttpStatus.CREATED);
    }

    @PatchMapping("{category-id}")
    public ResponseEntity<HttpStatus> patchSingleCategory(@RequestBody CategoryDto.Patch categoryDtoPatch,
                                              @PathVariable("category-id") @Min(value = 2) long categoryId) {
        categoryDtoPatch.setCategoryId(categoryId);
        Category category = mapper.categoryDtoPatchToCategory(categoryDtoPatch);
        categoryService.editSingleCategory(category);

        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("{category-id}")
    public ResponseEntity<SingleResponseDto> getSingleCategory(@PathVariable("category-id") @Positive long categoryId) {
        Category singleCategory = categoryService.findSingleCategory(categoryId);

        return ResponseEntity.ok(new SingleResponseDto(singleCategory));
    }

    @GetMapping()
    public ResponseEntity<SingleResponseDto> getAllBlogsInMainCategory() {
        Category singleCategory = categoryService.findSingleCategory(MAIN_CATEGORY_ID);
        CategoryResponseDto categoryResponseDto = mapper.categoryToCategoryResponseDto(singleCategory);

        return ResponseEntity.ok(new SingleResponseDto(categoryResponseDto));
    }

    @DeleteMapping("{category-id}")
    public ResponseEntity<HttpStatus> deleteSingleCategory(@PathVariable("category-id") @Min(value = 2) long categoryId) {
        categoryService.deleteSingleCategory(categoryId);

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
