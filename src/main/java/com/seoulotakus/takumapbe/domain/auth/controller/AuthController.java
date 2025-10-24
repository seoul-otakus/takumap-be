package com.seoulotakus.takumapbe.domain.auth.controller;

import com.seoulotakus.takumapbe.domain.user.dto.response.UserDetailDTO;
import com.seoulotakus.takumapbe.domain.user.entity.UserEntity;
import com.seoulotakus.takumapbe.global.config.PrincipalDetails;
import com.seoulotakus.takumapbe.global.exception.BusinessException;
import com.seoulotakus.takumapbe.global.exception.ErrorCode;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    @GetMapping("/login/test")
    public @RequestBody String loginTest(Authentication authentication, @AuthenticationPrincipal UserDetails userDetails){
        // DI(의존성 주입)을 하면 getPrincipal은 Object를 반환하기 때문에 (PrincipalDetails)로 다운 캐스팅을 하면 User정보를 받을 수 있다.
        System.out.println("======/test/login==========");
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        System.out.println("authentication :" + principalDetails.getUser());

        // @AuthenticationPrincipal를 통해서 세션 정보에 접근할 수 있다.
        // 타입은 PrincipalDetails가 UserDetails를 implements 했기 때문에 PrincipalDetails로 가능
        // PrincipalDetails로 타입을 지정하면 principalDetails.getUser()로 접근 가능
        System.out.println("userDetails :" + userDetails.getUsername());
        return "세션 정보 확인하기";
    }

    @GetMapping("/test/oauth/login")
    public @RequestBody String testOAuthLogin(Authentication authentication, @AuthenticationPrincipal OAuth2User oauth){
        System.out.println("======/test/login==========");
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        System.out.println("authentication :" + oAuth2User.getAttributes());
        System.out.println("oauth2User : " + oauth.getAttributes());

        return "OAuth 세션 정보 확인하기";
    }

    /**
     * spring security는 자기만의 security session을 가진다.
     * 원래 서버가 가지는 session이 있고 그 안에 security가 관리하는 session이 있다.
     * security session 안에는 Authentication객체만 들어갈 수 있다.
     * Authentication 객체 안에 DI로 UserDetails타입과 OAuth2User 타입이 들어갈 수 있다.
     * 즉, Security가 들고있는 session에는 무조건 Authentication만 들어갈 수 있다.
     *     그리고 Authentication이 들어간 순간 로그인이 된 것이다.
     *     근데 Authentication 객체 안에 들어갈 타입은 UserDetails타입과 OAuth2User 타입이 있다.
     *     일반적인 로그인을 할 때는 UserDetails 타입으로 Authentication 안에 들어가며
     *     OAuth 로그인을 할 때는 OAuth2User 타입이 Authentication 안에 들어간다.
     * **/

    /** 소셜 로그인 **/
    // 현재 로그인된 사용자 정보 조회
    // 인증된 사용자만 접근 가능하도록 SecurityConfig에서 설정
   @GetMapping("/oauth/me")
    public UserDetailDTO getLoggedInUser(@AuthenticationPrincipal PrincipalDetails principalDetails){
       if(principalDetails == null){
           // 인증되지 않은 경우 처리
           throw new BusinessException(ErrorCode.UNAUTHORIZED, "인증되지 않은 사용자입니다.");
       }

       // UserEntity를 DTO로 변환하여 반환
       return convertToDto(principalDetails.getUser());
   }

   private UserDetailDTO convertToDto(UserEntity user){
       return new UserDetailDTO().builder()
               .id(user.getId())
               .nickname(user.getNickname())
               .userId(user.getUserId())
               .password(user.getPassword())
               .email(user.getEmail())
               .userRole(user.getUserRole())
               .provider(user.getProvider())
               .providerId(user.getProviderId())
               .isActive(user.getIsActive())
               .build();
   }

    /** 로그아웃 **/


    /** 회원가입 **/


}
