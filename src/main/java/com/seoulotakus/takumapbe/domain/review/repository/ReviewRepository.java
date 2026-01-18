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
     * [Backoffice] 사장님(ownerId)이 보유한 Shop에 작성된 리뷰들만 조회
     * 조건 1: Review(r)와 Shop(s)을 조인
     * 조건 2: Shop의 생성자(s.createdBy.id)가 현재 로그인한 유저(ownerId)와 같아야 함
     * 조건 3: 삭제되지 않은 리뷰만 조회
     */
    @Query(value = """
            SELECT r FROM Review r
                JOIN FETCH r.shop s
                JOIN FETCH r.writer w
            WHERE s.createdBy.id = :ownerId
              AND r.deletedAt IS NULL
    """,
            countQuery = "SELECT COUNT(r) FROM Review r JOIN r.shop s WHERE s.createdBy.id = :ownerId AND r.deletedAt IS NULL")
    Page<Review> findAllByShopOwnerId(@Param("ownerId") Long ownerId, Pageable pageable);
}
