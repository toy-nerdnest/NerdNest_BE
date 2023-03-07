package com.server.domain.category.service;

import com.server.domain.category.entity.Category;
import com.server.domain.category.repository.CategoryRepository;
import com.server.domain.member.service.MemberService;
import com.server.exception.BusinessLogicException;
import com.server.exception.ExceptionCode;
import com.server.utils.CustomBeanUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.validation.constraints.Positive;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    private final CustomBeanUtils beanUtils;

    private final MemberService memberService;

    public void makeSingleCategory(Category category) {
        verifyCategoryNameExistence(category);
        categoryRepository.save(category);
    }

    private void verifyCategoryNameExistence(Category category) {
        Optional<Category> optionalCategory = categoryRepository.findByCategoryName(category.getCategoryName());

        if (optionalCategory.isPresent()) {
            throw new BusinessLogicException(ExceptionCode.CATEGORY_EXISTS);
        }
    }

    public void editSingleCategory(Category category) {
        Category singleCategory = findSingleCategoryById(category.getCategoryId());
        beanUtils.copyNonNullProperties(category, singleCategory);

        categoryRepository.save(singleCategory);
    }

    public Category findSingleCategoryById(long categoryId) {
        return verifyCategoryById(categoryId);
    }

    public Category findSingleCategoryByName(String categoryName) {
        return verifyCategoryByName(categoryName);
    }

    public List<Category> findAllCategoriesEachMember(@Positive long memberId) {
        return categoryRepository.findAllByMember(memberService.findMember(memberId));
    }

    public Page<Category> findAllCategories(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);

        return categoryRepository.findAll(pageable);
    }

    public void deleteSingleCategory(long categoryId) {
        Category category = verifyCategoryById(categoryId);
        categoryRepository.delete(category);
    }

    private Category verifyCategoryById(long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.CATEGORY_NOT_FOUND));
    }

    private Category verifyCategoryByName(String categoryName) {
        return categoryRepository.findByCategoryName(categoryName)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.CATEGORY_NOT_FOUND));
    }

}
