package com.seoulotakus.takumapbe.domain.user.controller;

import com.seoulotakus.takumapbe.domain.user.dto.response.UserInfoResponseDTO;
import com.seoulotakus.takumapbe.domain.user.entity.UserEntity;
import com.seoulotakus.takumapbe.global.response.ApiResponse;
import com.seoulotakus.takumapbe.global.util.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    /** 사용자 정보 조회 **/
    // @CurrentUser를 통해 현재 로그인된 사용자 정보를 가져와 UserInfoResponseDTO에 담아 반환.
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserInfoResponseDTO>> getMyInfo(@CurrentUser UserEntity user) {
        UserInfoResponseDTO responseDTO = new UserInfoResponseDTO(user);
        return ResponseEntity.ok(ApiResponse.success(responseDTO, "내 정보 조회에 성공했습니다."));
    }
}