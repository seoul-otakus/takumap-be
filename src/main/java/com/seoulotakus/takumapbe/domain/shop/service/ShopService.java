package com.seoulotakus.takumapbe.domain.shop.service;

import com.seoulotakus.takumapbe.domain.shop.dto.request.ShopCreateRequest;
import com.seoulotakus.takumapbe.domain.shop.dto.request.ShopUpdateRequest; // CreateRequest와 필드가 같다면 재사용 가능하지만 분리 추천
import com.seoulotakus.takumapbe.domain.shop.dto.response.ShopDetailResponse;
import com.seoulotakus.takumapbe.domain.shop.entity.Category;
import com.seoulotakus.takumapbe.domain.shop.entity.Shop;
import com.seoulotakus.takumapbe.domain.shop.repository.CategoryRepository; // Category Repo 필요
import com.seoulotakus.takumapbe.domain.shop.repository.ShopRepository;
import com.seoulotakus.takumapbe.domain.user.entity.UserEntity;
import com.seoulotakus.takumapbe.global.exception.BusinessException;
import com.seoulotakus.takumapbe.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShopService {

    private final ShopRepository shopRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public ShopDetailResponse createShop(ShopCreateRequest request, UserEntity user) {
        log.info("샵 생성 시도 - 사용자 ID: {}", user.getId());

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND)); // ErrorCode.CATEGORY_NOT_FOUND 권장

        Shop shop = Shop.builder()
                .category(category)
                .name(request.getName())
                .address(request.getAddress())
                .description(request.getDescription())
                .phoneNumber(request.getPhoneNumber())
                .openTime(request.getOpenTime())
                .closeTime(request.getCloseTime())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .createdBy(user)
                .build();

        Shop savedShop = shopRepository.save(shop);
        return new ShopDetailResponse(savedShop);
    }

    public ShopDetailResponse getShopById(Long shopId) {
        Shop shop = shopRepository.findByIdWithDetails(shopId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

        return new ShopDetailResponse(shop);
    }

    public Page<ShopDetailResponse> getShops(Pageable pageable) {
        Page<Shop> shops = shopRepository.findAllWithCategory(pageable);
        return shops.map(ShopDetailResponse::new);
    }

    @Transactional
    public ShopDetailResponse updateShop(Long shopId, ShopUpdateRequest request, UserEntity user) {
        log.info("샵 수정 시도 ID : {} 수정자 : {}", shopId, user.getId());

        Shop shop = shopRepository.findByIdAndDeletedAtIsNull(shopId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

        // 권한 체크
        checkOwner(shop, user);

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

        shop.update(
                category,
                request.getName(),
                request.getAddress(),
                request.getDescription(),
                request.getPhoneNumber(),
                request.getOpenTime(),
                request.getCloseTime(),
                request.getLatitude(),
                request.getLongitude(),
                user
        );

        return new ShopDetailResponse(shop);
    }

    @Transactional
    public void deleteShop(Long shopId, UserEntity user) {
        log.info("샵 삭제 시도 ID : {} 사용자 : {}", shopId, user.getId());

        Shop shop = shopRepository.findByIdAndDeletedAtIsNull(shopId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

        checkOwner(shop, user);

        shop.softDelete(user);
    }

    private void checkOwner(Shop shop, UserEntity user) {
        // 관리자가 아니라면 본인이 생성한 샵만 수정/삭제 가능하도록 제한
        if (shop.getCreatedBy() == null || shop.getCreatedBy().getId() != user.getId()) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
    }
}