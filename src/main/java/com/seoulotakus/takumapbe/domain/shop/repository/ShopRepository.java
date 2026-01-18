package com.seoulotakus.takumapbe.domain.shop.repository;

import com.seoulotakus.takumapbe.domain.shop.entity.Shop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ShopRepository extends JpaRepository<Shop, Long> {

    // 상세 조회 시 Category와 작성자 정보를 한 번에 가져옴
    @Query("""
        SELECT s FROM Shop s
            JOIN FETCH s.category c
            LEFT JOIN FETCH s.createdBy u
        WHERE s.id = :shopId
          AND s.deletedAt IS NULL
    """)
    Optional<Shop> findByIdWithDetails(Long shopId);

    // 목록 조회 (카테고리 필터링이 필요할 경우 사용 가능, 여기선 전체 목록 예시)
    @Query(value = """
        SELECT s FROM Shop s
            JOIN FETCH s.category c
        WHERE s.deletedAt IS NULL
    """,
            countQuery = "SELECT COUNT(s) FROM Shop s WHERE s.deletedAt IS NULL")
    Page<Shop> findAllWithCategory(Pageable pageable);

    // 삭제되지 않은 샵 조회
    Optional<Shop> findByIdAndDeletedAtIsNull(Long shopId);
}