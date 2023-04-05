package com.server.domain.category.repository;


import com.server.domain.category.entity.Category;
import com.server.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.validation.constraints.Positive;
import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByCategoryNameAndMember(String categoryName, Member member);
    List<Category> findAllByMember(Member member);
    Page<Category> findAll(Pageable pageable);
}
