package com.seoulotakus.takumapbe.domain.bookmark.repository;

import com.seoulotakus.takumapbe.domain.bookmark.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    /**
     * 특정 사용자의 특정 가게 즐겨찾기 여부 확인
     */
    boolean existsByUserIdAndShopId(Long userId, Long shopId);

    /**
     * 특정 사용자의 특정 가게 즐겨찾기 조회
     */
    Optional<Favorite> findByUserIdAndShopId(Long userId, Long shopId);

    /**
     * 특정 사용자의 즐겨찾기된 가게 ID 목록 조회 (성능 최적화용)
     */
    @Query("SELECT f.shop.id FROM Favorite f WHERE f.user.id = :userId")
    List<Long> findShopIdsByUserId(@Param("userId") Long userId);

    /**
     * 특정 사용자의 즐겨찾기 삭제
     */
    void deleteByUserIdAndShopId(Long userId, Long shopId);
}
