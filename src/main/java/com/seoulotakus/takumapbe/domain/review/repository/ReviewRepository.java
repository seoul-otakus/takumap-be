package com.seoulotakus.takumapbe.domain.review.repository;

import com.seoulotakus.takumapbe.domain.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query("""
        SELECT r FROM Review r
            JOIN FETCH r.writer w
            JOIN FETCH r.shop s
        WHERE r.id = :reviewId
          AND r.deletedAt IS NULL
        """)
    Optional<Review> findByIdWithDetails(Long reviewId);

    @Query(value = """
            SELECT r FROM Review r
                JOIN FETCH r.writer w
            WHERE r.shop.id = :shopId
              AND r.deletedAt IS NULL
    """,
    countQuery = "SELECT COUNT(r) FROM Review r WHERE r.shop.id = :shopId AND r.deletedAt IS NULL")
    Page<Review> findByShopIdWithWriter(Long shopId, Pageable pageable);

    Optional<Review> findByIdAndDeletedAtIsNull(Long reviewId);

    /**
     * 특정 유저가 작성한 모든 리뷰 조회 (관리자의 유저 삭제 시 사용)
     */
    @Query("""
        SELECT r FROM Review r
        WHERE r.writer.id = :userId
    """)
    List<Review> findAllByWriterId(@Param("userId") Long userId);
}
