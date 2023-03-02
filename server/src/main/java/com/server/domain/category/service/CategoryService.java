package com.server.domain.category.service;

import com.fasterxml.jackson.databind.util.BeanUtil;
import com.server.domain.category.entity.Category;
import com.server.domain.category.repository.CategoryRepository;
import com.server.exception.BusinessLogicException;
import com.server.exception.ExceptionCode;
import com.server.utils.CustomBeanUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    private final CustomBeanUtils beanUtils;

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
        Category singleCategory = findSingleCategory(category.getCategoryId());
        beanUtils.copyNonNullProperties(category, singleCategory);

        categoryRepository.save(singleCategory);
    }

    public Category findSingleCategory(long categoryId) {
        return verifyCategoryById(categoryId);
    }

    public void deleteSingleCategory(long categoryId) {
        Category category = verifyCategoryById(categoryId);
        categoryRepository.delete(category);
    }

    private Category verifyCategoryById(long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.CATEGORY_NOT_FOUND));
    }
}
