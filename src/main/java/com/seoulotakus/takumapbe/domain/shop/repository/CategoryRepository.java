package com.seoulotakus.takumapbe.domain.shop.repository;

import com.seoulotakus.takumapbe.domain.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
