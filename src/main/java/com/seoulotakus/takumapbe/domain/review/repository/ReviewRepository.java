package com.seoulotakus.takumapbe.domain.review.repository;

import com.seoulotakus.takumapbe.domain.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findByShopId(Long shopId, Pageable pageable);
}
