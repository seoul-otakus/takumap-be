package com.seoulotakus.takumapbe.domain.user.controller;

import com.seoulotakus.takumapbe.domain.user.dto.request.FindIdRequestDTO;
import com.seoulotakus.takumapbe.domain.user.dto.request.PasswordResetConfirmRequestDTO;
import com.seoulotakus.takumapbe.domain.user.dto.request.PasswordResetRequestDTO;
import com.seoulotakus.takumapbe.domain.user.dto.response.FindIdResponseDTO;
import com.seoulotakus.takumapbe.domain.user.dto.response.UserInfoResponseDTO;
import com.seoulotakus.takumapbe.domain.user.entity.UserEntity;
import com.seoulotakus.takumapbe.domain.user.service.UserService;
import com.seoulotakus.takumapbe.global.response.ApiResponse;
import com.seoulotakus.takumapbe.global.util.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /** 사용자 정보 조회 **/
    // @CurrentUser를 통해 현재 로그인된 사용자 정보를 가져와 UserInfoResponseDTO에 담아 반환.
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserInfoResponseDTO>> getMyInfo(@CurrentUser UserEntity user) {
        UserInfoResponseDTO responseDTO = new UserInfoResponseDTO(user);
        return ResponseEntity.ok(ApiResponse.success(responseDTO, "내 정보 조회에 성공했습니다."));
    }

    /** 아이디 찾기 **/
    /* 이메일 입력하면 이메일 찾아서 해당 아이디를 화면에 보여주기 */
    @GetMapping("/find-id")
    public ResponseEntity<ApiResponse<FindIdResponseDTO>> getMyId(@RequestBody FindIdRequestDTO email){

        String userId = userService.findIdByEmail(email.getEmail());
        FindIdResponseDTO responseDTO = new FindIdResponseDTO(userId);

        return ResponseEntity.ok(ApiResponse.success(responseDTO, "아이지 조회 성공"));
    }

    /** 비밀번호 재설정 - 임시비밀번호 전송 **/
    /*
    * 1. 아이디, 이메일 받기
    * 2. 아이디와 이메일로 회원 존재하는지 확인하기
    * 3. 존재하지 않으면 해당 에러메시지
    * 4. 존재하면 해당 이메일로 임시 비밀번호 전송
    * 5. 성공 메시지 보내기
    */
    @PostMapping("/sent-onetime-password")
    public ResponseEntity<ApiResponse<?>> sendOneTimePasswordEmail(@RequestBody PasswordResetRequestDTO requestDTO){

        String userId = requestDTO.getUserId();
        String email = requestDTO.getEmail();

        userService.sendOneTimePasswordEmail(userId, email);

        return ResponseEntity.ok(ApiResponse.success(null, "임시 비밀번호를 이메일로 전송했습니다."));
    }

    /** 비밀번호 재설정 - 임시비밀번호 확인 및 비밀번호 변경 **/
    /*
    * 1. 임시 비밀번호 맞는지 확인
    * 2. 임시 비밀번호가 안맞으면 에러 메시지
    * 3. 맞으면 email에 해당하는 유저 정보 찾기
    * 4. 새 비밀번호 설정
    */
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<?>> resetNewPassword(@RequestBody PasswordResetConfirmRequestDTO requestDTO){

        userService.resetNewPassword(requestDTO);
        return ResponseEntity.ok(ApiResponse.success(null, "비밀번호 재설정이 완료되었습니다."));
    }
}