package com.seoulotakus.takumapbe.domain.user.controller;

import com.seoulotakus.takumapbe.domain.user.dto.response.UserListResponse;
import com.seoulotakus.takumapbe.domain.user.service.UserService;
import com.seoulotakus.takumapbe.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    /**
     * 모든 사용자 목록 조회 (관리자 전용, 페이징 처리)
     * @PreAuthorize 어노테이션으로 ROLE_ADMIN만 접근 가능
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<UserListResponse>>> getAllUsers(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("관리자 사용자 목록 조회 요청 - 페이지: {}, 크기: {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<UserListResponse> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(ApiResponse.success(users, "사용자 목록 조회 성공"));
    }

    /**
     * 사용자 및 해당 사용자의 모든 리뷰 삭제 (관리자 전용, Hard Delete)
     * @PreAuthorize 어노테이션으로 ROLE_ADMIN만 접근 가능
     */
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long userId) {
        log.info("관리자 사용자 삭제 요청 - 사용자 ID: {}", userId);
        userService.deleteUserAndReviews(userId);
        return ResponseEntity.ok(ApiResponse.success(null, "사용자 및 관련 리뷰 삭제 성공"));
    }
}