package com.seoulotakus.takumapbe.domain.user.service;

import com.seoulotakus.takumapbe.common.auth.entity.CertificationEntity;
import com.seoulotakus.takumapbe.common.auth.provider.mail.EmailProvider;
import com.seoulotakus.takumapbe.common.auth.repository.CertificationRepository;
import com.seoulotakus.takumapbe.domain.review.repository.ReviewRepository;
import com.seoulotakus.takumapbe.domain.user.dto.request.PasswordResetConfirmRequestDTO;
import com.seoulotakus.takumapbe.domain.user.entity.UserEntity;
import com.seoulotakus.takumapbe.domain.user.repository.UserRepository;
import com.seoulotakus.takumapbe.global.exception.BusinessException;
import com.seoulotakus.takumapbe.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.seoulotakus.takumapbe.domain.review.entity.Review;
import com.seoulotakus.takumapbe.domain.user.dto.response.UserListResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.security.SecureRandom;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final EmailProvider emailProvider;
    private final CertificationRepository certificationRepository;
    private final PasswordEncoder passwordEncoder;

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
//        // 1. 유저 존재 확인
//        UserEntity user = userRepository.findById(userId)
//                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "사용자를 찾을 수 없습니다."));
//
//        log.info("사용자 삭제 시작 - ID: {}, 닉네임: {}", userId, user.getNickname());
//
//        // 2. 해당 유저가 작성한 모든 리뷰 조회
//        List<Review> userReviews = reviewRepository.findAllByWriterId(userId);
//        log.info("삭제할 리뷰 개수: {}", userReviews.size());
//
//        // 3. FK 제약 조건 때문에 리뷰를 먼저 삭제해야 함
//        if (!userReviews.isEmpty()) {
//            reviewRepository.deleteAll(userReviews);
//            log.info("리뷰 삭제 완료: {} 개", userReviews.size());
//        }
//
//        // 4. 유저 삭제 (Hard Delete)
//        userRepository.delete(user);
//        log.info("사용자 삭제 완료 - ID: {}", userId);
    }

    /** 아이디 찾기 **/
    public String findIdByEmail(String email) {

        UserEntity user = userRepository.findIdByEmail(email).orElseThrow(() -> new BusinessException(ErrorCode.EMAIL_NOT_FOUND));
        String userId = user.getUserId();

        return userId;
    }

    /** 비밀번호 재설정 - 임시비밀번호 전송 **/
    public void sendOneTimePasswordEmail(String userId, String email) {


        UserEntity user = userRepository.findByUserIdAndEmail(userId, email).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        String oneTimePassword = createOneTimePassword();

        boolean isSuccessed = emailProvider.sendOneTimePasswordMail(email, oneTimePassword);

        if(!isSuccessed){
            throw new BusinessException(ErrorCode.MAIL_FAIL);
        }

        CertificationEntity certificationEntity = CertificationEntity.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .certificationNumber(oneTimePassword)
                .build();
        certificationRepository.save(certificationEntity);
    }

    /** 임시비밀번호 생성 **/
    private String createOneTimePassword(){
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789~!@#$%^&*()_+-=";
        String oneTimePassword = "";
        SecureRandom random = new SecureRandom();
        StringBuilder stringBuilder = new StringBuilder();

        for(int i = 0; i < 9; i++){
            // 무작위로 문자열의 인덱스 반환
            int index = random.nextInt(chars.length());
            // index의 위치한 랜덤값으로 새로운 임시비밀번호 문자열 생성
            stringBuilder.append(chars.charAt(index));
        }

        return stringBuilder.toString();
    }

    @Transactional
    public void resetNewPassword(PasswordResetConfirmRequestDTO requestDTO) {

        String userId = requestDTO.getUserId();
        String userEmail = requestDTO.getEmail();
        String oneTimePassword = requestDTO.getOneTimePassword();
        String newPassword = requestDTO.getNewPassword();

        CertificationEntity certificationEntity = certificationRepository.findByUserId(userId);
        if(certificationEntity == null){
            throw new BusinessException(ErrorCode.NOT_FOUND);
        }

        boolean isMatched = certificationEntity.getEmail().equals(userEmail) && certificationEntity.getCertificationNumber().equals(oneTimePassword);

        if(!isMatched){
            throw new BusinessException(ErrorCode.CERTIFICATION_PASSWORD_FAIL);
        }

        UserEntity userEntity = userRepository.findByUserId(userId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        String encodedNewPassword = passwordEncoder.encode(newPassword);
        userEntity.setPassword(encodedNewPassword);

        // 인증 완료 후 해당 엔티티 삭제하기 - 보안상의 이유
        certificationRepository.delete(certificationEntity);
    }
}
