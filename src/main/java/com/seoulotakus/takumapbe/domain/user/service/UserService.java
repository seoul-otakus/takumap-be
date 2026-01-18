package com.seoulotakus.takumapbe.domain.user.service;

import com.seoulotakus.takumapbe.domain.user.dto.request.FindIdRequestDTO;
import com.seoulotakus.takumapbe.domain.user.entity.UserEntity;
import com.seoulotakus.takumapbe.domain.user.repository.UserRepository;
import com.seoulotakus.takumapbe.global.exception.BusinessException;
import com.seoulotakus.takumapbe.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public String findIdByEmail(String email) {

        UserEntity user = userRepository.findIdByEmail(email).orElseThrow(() -> new BusinessException(ErrorCode.EMAIL_NOT_FOUND));
        String userId = user.getUserId();

        return userId;
    }

    public void sendOneTimePasswordEmail(String userId, String email) {


        UserEntity user = userRepository.findByUserIdAndEmail(userId, email).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        String oneTimePassword = createOneTimePassword();

    }

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
}
