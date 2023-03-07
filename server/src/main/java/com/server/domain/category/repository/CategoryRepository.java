package com.server.domain.category.repository;


import com.server.domain.blog.entity.Blog;
import com.server.domain.category.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByCategoryName(String categoryName);
    Page<Blog> findAllByCategoryId(long categoryId, Pageable pageable);
}
