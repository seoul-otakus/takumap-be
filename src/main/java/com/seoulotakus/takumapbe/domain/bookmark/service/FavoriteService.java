package com.seoulotakus.takumapbe.domain.bookmark.service;

import com.seoulotakus.takumapbe.domain.bookmark.entity.Favorite;
import com.seoulotakus.takumapbe.domain.bookmark.repository.FavoriteRepository;
import com.seoulotakus.takumapbe.domain.shop.entity.Shop;
import com.seoulotakus.takumapbe.domain.shop.repository.ShopRepository;
import com.seoulotakus.takumapbe.domain.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final ShopRepository shopRepository;

    /**
     * 즐겨찾기 토글 (추가/해제)
     * @param shopId 가게 ID
     * @param user 현재 사용자
     * @return 즐겨찾기 추가 여부 (true: 추가됨, false: 해제됨)
     */
    @Transactional
    public boolean toggleFavorite(Long shopId, UserEntity user) {
        boolean isFavorited = favoriteRepository.existsByUserIdAndShopId(user.getId(), shopId);

        if (isFavorited) {
            // 이미 즐겨찾기한 경우 -> 해제
            favoriteRepository.deleteByUserIdAndShopId(user.getId(), shopId);
            return false;
        } else {
            // 즐겨찾기하지 않은 경우 -> 추가
            Shop shop = shopRepository.findById(shopId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 가게입니다. Shop ID: " + shopId));

            Favorite favorite = Favorite.builder()
                    .user(user)
                    .shop(shop)
                    .build();

            favoriteRepository.save(favorite);
            return true;
        }
    }

    /**
     * 사용자의 즐겨찾기된 가게 ID 목록 조회
     * @param userId 사용자 ID
     * @return 즐겨찾기된 가게 ID 목록
     */
    public List<Long> getFavoriteShopIds(Long userId) {
        return favoriteRepository.findShopIdsByUserId(userId);
    }
}
