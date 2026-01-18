package com.seoulotakus.takumapbe.domain.bookmark.controller;

import com.seoulotakus.takumapbe.domain.bookmark.service.FavoriteService;
import com.seoulotakus.takumapbe.domain.user.entity.UserEntity;
import com.seoulotakus.takumapbe.global.response.ApiResponse;
import com.seoulotakus.takumapbe.global.util.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    /**
     * 즐겨찾기 토글 (추가/해제)
     * POST /api/v1/favorites/{shopId}/toggle
     *
     * @param shopId 가게 ID
     * @param user 현재 로그인한 사용자
     * @return 즐겨찾기 추가 여부 (true: 추가됨, false: 해제됨)
     */
    @PostMapping("/{shopId}/toggle")
    public ResponseEntity<ApiResponse<Boolean>> toggleFavorite(
            @PathVariable Long shopId,
            @CurrentUser UserEntity user) {
        boolean isFavorited = favoriteService.toggleFavorite(shopId, user);
        String message = isFavorited ? "즐겨찾기에 추가되었습니다." : "즐겨찾기가 해제되었습니다.";
        return ResponseEntity.ok(ApiResponse.success(isFavorited, message));
    }

    /**
     * 현재 사용자의 즐겨찾기된 가게 ID 목록 조회
     * GET /api/v1/favorites/shop-ids
     *
     * @param user 현재 로그인한 사용자
     * @return 즐겨찾기된 가게 ID 목록
     */
    @GetMapping("/shop-ids")
    public ResponseEntity<ApiResponse<List<Long>>> getFavoriteShopIds(
            @CurrentUser UserEntity user) {
        List<Long> shopIds = favoriteService.getFavoriteShopIds(user.getId());
        return ResponseEntity.ok(ApiResponse.success(shopIds, "즐겨찾기 목록 조회 성공"));
    }
}
