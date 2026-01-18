package com.seoulotakus.takumapbe.domain.user.service;

import com.seoulotakus.takumapbe.domain.review.entity.Review;
import com.seoulotakus.takumapbe.domain.review.repository.ReviewRepository;
import com.seoulotakus.takumapbe.domain.user.dto.response.UserListResponse;
import com.seoulotakus.takumapbe.domain.user.entity.UserEntity;
import com.seoulotakus.takumapbe.domain.user.repository.UserRepository;
import com.seoulotakus.takumapbe.global.exception.BusinessException;
import com.seoulotakus.takumapbe.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;

    /**
     * 모든 사용자 목록 조회 (관리자 전용, 페이징 처리)
     */
    public Page<UserListResponse> getAllUsers(Pageable pageable) {
        Page<UserEntity> users = userRepository.findAll(pageable);
        return users.map(UserListResponse::from);
    }

    /**
     * 사용자 및 해당 사용자의 모든 리뷰 삭제 (Hard Delete)
     * @Transactional 어노테이션으로 원자성 보장:
     * - 리뷰 삭제 실패 시 유저도 삭제되지 않음
     * - 유저 삭제 실패 시 리뷰 삭제도 롤백됨
     */
    @Transactional
    public void deleteUserAndReviews(Long userId) {
        // 1. 유저 존재 확인
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        log.info("사용자 삭제 시작 - ID: {}, 닉네임: {}", userId, user.getNickname());

        // 2. 해당 유저가 작성한 모든 리뷰 조회
        List<Review> userReviews = reviewRepository.findAllByWriterId(userId);
        log.info("삭제할 리뷰 개수: {}", userReviews.size());

        // 3. FK 제약 조건 때문에 리뷰를 먼저 삭제해야 함
        if (!userReviews.isEmpty()) {
            reviewRepository.deleteAll(userReviews);
            log.info("리뷰 삭제 완료: {} 개", userReviews.size());
        }

        // 4. 유저 삭제 (Hard Delete)
        userRepository.delete(user);
        log.info("사용자 삭제 완료 - ID: {}", userId);
    }
