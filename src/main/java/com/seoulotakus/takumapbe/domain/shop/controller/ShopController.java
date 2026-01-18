package com.seoulotakus.takumapbe.domain.shop.controller;

import com.seoulotakus.takumapbe.domain.shop.dto.request.ShopCreateRequest;
import com.seoulotakus.takumapbe.domain.shop.dto.request.ShopUpdateRequest;
import com.seoulotakus.takumapbe.domain.shop.dto.response.ShopDetailResponse;
import com.seoulotakus.takumapbe.domain.shop.service.ShopService;
import com.seoulotakus.takumapbe.domain.user.entity.UserEntity;
import com.seoulotakus.takumapbe.global.response.ApiResponse;
import com.seoulotakus.takumapbe.global.util.CurrentUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/shops")
public class ShopController {

    private final ShopService shopService;

    @PostMapping
    public ResponseEntity<ApiResponse<ShopDetailResponse>> createShop(
            @Valid @RequestBody ShopCreateRequest request,
            @CurrentUser UserEntity user
    ) {
        ShopDetailResponse response = shopService.createShop(request, user);
        return ResponseEntity.ok(ApiResponse.success(response, "상점 등록 성공"));
    }

    @GetMapping("/{shopId}")
    public ResponseEntity<ApiResponse<ShopDetailResponse>> getShopById(
            @PathVariable Long shopId
    ) {
        ShopDetailResponse response = shopService.getShopById(shopId);
        return ResponseEntity.ok(ApiResponse.success(response, "상점 상세 조회 성공"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ShopDetailResponse>>> getShops(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<ShopDetailResponse> response = shopService.getShops(pageable);
        return ResponseEntity.ok(ApiResponse.success(response, "상점 목록 조회 성공"));
    }

    @PutMapping("/{shopId}")
    public ResponseEntity<ApiResponse<ShopDetailResponse>> updateShop(
            @PathVariable Long shopId,
            @Valid @RequestBody ShopUpdateRequest request,
            @CurrentUser UserEntity user
    ) {
        ShopDetailResponse response = shopService.updateShop(shopId, request, user);
        return ResponseEntity.ok(ApiResponse.success(response, "상점 수정 성공"));
    }

    @DeleteMapping("/{shopId}")
    public ResponseEntity<ApiResponse<Void>> deleteShop(
            @PathVariable Long shopId,
            @CurrentUser UserEntity user
    ) {
        shopService.deleteShop(shopId, user);
        return ResponseEntity.ok(ApiResponse.success(null, "상점 삭제 성공"));
    }
}