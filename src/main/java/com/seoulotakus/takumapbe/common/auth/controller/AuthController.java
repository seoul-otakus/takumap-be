package com.seoulotakus.takumapbe.common.auth.controller;

import com.seoulotakus.takumapbe.common.auth.dto.request.*;
import com.seoulotakus.takumapbe.common.auth.dto.response.AccessTokenResponseDTO;
import com.seoulotakus.takumapbe.common.auth.dto.response.LoginResponseDTO;
import com.seoulotakus.takumapbe.common.auth.service.AuthService;
import com.seoulotakus.takumapbe.domain.user.dto.response.CurrentUserResponse;
import com.seoulotakus.takumapbe.domain.user.entity.UserEntity;
import com.seoulotakus.takumapbe.global.response.ApiResponse;
import com.seoulotakus.takumapbe.global.util.CurrentUser;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    /** id 중복 체크 **/
    @PostMapping("/id-check")
    public ResponseEntity<ApiResponse<?>> idCheck(@RequestBody @Valid IdCheckRequestDTO requestDTO){
        authService.idCheck(requestDTO);
        return ResponseEntity.ok(ApiResponse.success(null, "사용 가능한 아이디입니다."));
    }

    /** 닉네입 중복 체크 **/
    @PostMapping("/nickname-check")
    public ResponseEntity<ApiResponse<?>> nicknameCheck(@RequestBody @Valid NicknameCheckRequestDTO requestDTO){
        authService.nicknameCheck(requestDTO);
        return ResponseEntity.ok(ApiResponse.success(null, "사용 가능한 닉네임입니다."));
    }

    /** 이메일 인증 번호 발송 **/
    @PostMapping("/email-certification")
    public ResponseEntity<ApiResponse<?>> emailCertification(
            @RequestBody @Valid EmailCertificationRequestDTO requestDTO) {
            // @ModelAttribute @Valid EmailCertificationRequestDTO requestDTO) {
        authService.certificateEmail(requestDTO);
        return ResponseEntity.ok(ApiResponse.success(null, "인증 이메일 발송에 성공했습니다."));
    }

    /** 이메일 인증 번호 확인 **/
    @PostMapping("/check-certification")
    public ResponseEntity<ApiResponse<?>> checkCertification(@RequestBody @Valid CheckCertificationRequestDTO requestDTO){
        authService.checkCertification(requestDTO);
        return ResponseEntity.ok(ApiResponse.success(null, "이메일 인증에 성공했습니다."));
    }

    /** 자체 회원가입 **/
    @PostMapping("/sign-up")
    public ResponseEntity<ApiResponse<?>> signUp(@Valid @RequestBody SignUpRequestDTO requestDTO){
        authService.signUp(requestDTO);
        return ResponseEntity.ok(ApiResponse.success(null, "회원가입에 성공했습니다."));
    }

    /** 자체 로그인 **/
    @PostMapping("/sign-in")
    public ResponseEntity<ApiResponse<?>> login(@Valid @RequestBody LoginRequestDTO requestDTO, HttpServletResponse response){

        LoginResponseDTO loginResponseDTO = authService.login(requestDTO);
        boolean isProduction = "prod".equals(activeProfile);

        // Access Token을 쿠키에 설정
        ResponseCookie accessTokenCookie = ResponseCookie.from("access_token", loginResponseDTO.getToken())
                .path("/")
                .maxAge(60 * 60)  // 1시간
                .secure(isProduction)
                .httpOnly(true)
                .sameSite(isProduction ? "None" : "Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());

        // Refresh Token을 쿠키에 설정
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh_token", loginResponseDTO.getRefreshToken())
                .path("/")
                .maxAge(7 * 24 * 60 * 60)  // 7일
                .secure(isProduction)
                .httpOnly(true)
                .sameSite(isProduction ? "None" : "Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        return ResponseEntity.ok(ApiResponse.success(loginResponseDTO, "로그인에 성공했습니다."));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<?>> refreshAccessToken(@CookieValue("refresh_token") String refreshToken, HttpServletResponse response){
        AccessTokenResponseDTO responseDTO = authService.refreshAccessToken(refreshToken);
        boolean isProduction = "prod".equals(activeProfile);

        // 새로운 Access Token을 쿠키에 설정
        ResponseCookie accessTokenCookie = ResponseCookie.from("access_token", responseDTO.getAccessToken())
                .path("/")
                .maxAge(60 * 60)  // 1시간
                .secure(isProduction)
                .httpOnly(true)
                .sameSite(isProduction ? "None" : "Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());

        return ResponseEntity.ok(ApiResponse.success(responseDTO, "AccessToken이 재발급되었습니다."));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<?>> logout(HttpServletResponse response, @CookieValue("refresh_token") String refreshToken){
        authService.logout(refreshToken);
        boolean isProduction = "prod".equals(activeProfile);

        // access_token 쿠키 삭제
        ResponseCookie accessTokenCookie = ResponseCookie.from("access_token", "")
                .path("/")
                .maxAge(0)
                .secure(isProduction)
                .httpOnly(true)
                .sameSite(isProduction ? "None" : "Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());

        // refresh_token 쿠키 삭제
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh_token", "")
                .path("/")
                .maxAge(0)
                .secure(isProduction)
                .httpOnly(true)
                .sameSite(isProduction ? "None" : "Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        // JSESSIONID 쿠키 삭제
        ResponseCookie jsessionidCookie = ResponseCookie.from("JSESSIONID", "")
                .path("/")
                .maxAge(0)
                .secure(isProduction)
                .httpOnly(true)
                .sameSite(isProduction ? "None" : "Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, jsessionidCookie.toString());

        return ResponseEntity.ok(ApiResponse.success("로그아웃에 성공했습니다."));
    }

    // 사용자 인증
    @GetMapping("/check")
    public ResponseEntity<ApiResponse<?>> checkAuth(){
        return ResponseEntity.ok(ApiResponse.success(null, "인증된 사용자입니다."));
    }

    /**
     * 현재 로그인한 사용자 정보 조회 (role 포함)
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<CurrentUserResponse>> getCurrentUser(@CurrentUser UserEntity user) {
        CurrentUserResponse response = CurrentUserResponse.from(user);
        return ResponseEntity.ok(ApiResponse.success(response, "사용자 정보 조회 성공"));
    }


//    @GetMapping("/login/test")
//    public @RequestBody String loginTest(Authentication authentication, @AuthenticationPrincipal UserDetails userDetails){
//        // DI(의존성 주입)을 하면 getPrincipal은 Object를 반환하기 때문에 (PrincipalDetails)로 다운 캐스팅을 하면 User정보를 받을 수 있다.
//        System.out.println("======/test/login==========");
//        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
//        System.out.println("authentication :" + principalDetails.getUser());
//
//        // @AuthenticationPrincipal를 통해서 세션 정보에 접근할 수 있다.
//        // 타입은 PrincipalDetails가 UserDetails를 implements 했기 때문에 PrincipalDetails로 가능
//        // PrincipalDetails로 타입을 지정하면 principalDetails.getUser()로 접근 가능
//        System.out.println("userDetails :" + userDetails.getUsername());
//        return "세션 정보 확인하기";
//    }
//
//    @GetMapping("/test/oauth/login")
//    public @RequestBody String testOAuthLogin(Authentication authentication, @AuthenticationPrincipal OAuth2User oauth){
//        System.out.println("======/test/login==========");
//        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
//        System.out.println("authentication :" + oAuth2User.getAttributes());
//        System.out.println("oauth2User : " + oauth.getAttributes());
//
//        return "OAuth 세션 정보 확인하기";
//    }
//
//    /**
//     * spring security는 자기만의 security session을 가진다.
//     * 원래 서버가 가지는 session이 있고 그 안에 security가 관리하는 session이 있다.
//     * security session 안에는 Authentication객체만 들어갈 수 있다.
//     * Authentication 객체 안에 DI로 UserDetails타입과 OAuth2User 타입이 들어갈 수 있다.
//     * 즉, Security가 들고있는 session에는 무조건 Authentication만 들어갈 수 있다.
//     *     그리고 Authentication이 들어간 순간 로그인이 된 것이다.
//     *     근데 Authentication 객체 안에 들어갈 타입은 UserDetails타입과 OAuth2User 타입이 있다.
//     *     일반적인 로그인을 할 때는 UserDetails 타입으로 Authentication 안에 들어가며
//     *     OAuth 로그인을 할 때는 OAuth2User 타입이 Authentication 안에 들어간다.
//     * **/
//
//    /** 소셜 로그인 **/
//    // 현재 로그인된 사용자 정보 조회
//    // 인증된 사용자만 접근 가능하도록 SecurityConfig에서 설정
//   @GetMapping("/oauth/me")
//    public UserDetailDTO getLoggedInUser(@AuthenticationPrincipal PrincipalDetails principalDetails){
//       if(principalDetails == null){
//           // 인증되지 않은 경우 처리
//           throw new BusinessException(ErrorCode.UNAUTHORIZED, "인증되지 않은 사용자입니다.");
//       }
//
//       // UserEntity를 DTO로 변환하여 반환
//       return convertToDto(principalDetails.getUser());
//   }
//
//   private UserDetailDTO convertToDto(UserEntity user){
//       return new UserDetailDTO().builder()
//               .id(user.getId())
//               .nickname(user.getNickname())
//               .userId(user.getUserId())
//               .password(user.getPassword())
//               .email(user.getEmail())
//               .userRole(user.getUserRole())
//               .provider(user.getProvider())
//               .providerId(user.getProviderId())
//               .isActive(user.getIsActive())
//               .build();
//   }
//
//    /** 로그아웃 **/
//
//
//    /** 회원가입 **/

}
