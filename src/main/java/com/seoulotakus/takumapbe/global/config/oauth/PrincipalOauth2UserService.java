package com.seoulotakus.takumapbe.global.config.oauth;

import com.seoulotakus.takumapbe.domain.user.entity.UserEntity;
import com.seoulotakus.takumapbe.domain.user.enums.Provider;
import com.seoulotakus.takumapbe.domain.user.enums.UserRole;
import com.seoulotakus.takumapbe.domain.user.repository.UserRepository;
import com.seoulotakus.takumapbe.global.config.PrincipalDetails;
import com.seoulotakus.takumapbe.global.config.oauth.dto.OAuthAttributes;
import com.seoulotakus.takumapbe.global.exception.BusinessException;
import com.seoulotakus.takumapbe.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    @Lazy
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private final UserRepository userRepository;

    public PrincipalOauth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    // 구글 로그인이 완료된 후의 뒤처리 진행
    // 구글로부터 받은 userRequest 데이터에 대한 후처리 되는 함수
    // 함수 종료 시 @AuthenticationPrincipal 어노테이션이 만들어진다
//    @Override
//    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException{
//        System.out.println("userRequest : " + userRequest);
//        // registrationId로 어떤 Oauth로 로그인했는지 확인 가능
//        System.out.println("userRequest.getClientRegistration() : " + userRequest.getClientRegistration());
//        System.out.println("userRequest.getClientRegistration().getRegistrationId() : " + userRequest.getClientRegistration().getRegistrationId());
//        System.out.println("userRequest.getAccessToken().getTokenValue() : " + userRequest.getAccessToken().getTokenValue());
//
//        // provider 별로 속성을 정규화
//        OAuthAttributes attributes = OAuthAttributes
//
//        OAuth2User oauth2User = super.loadUser(userRequest);
//        // 구글로그인 버튼 클릭 -> 구글 로그인 창 -> 로그인 진행 완료 -> code 리턴 받음(OAuth-Client 라이브러리가 받음)
//        // -> 받은 Code를 통해서 Access Token을 요청 -> Access Token을 받음 ===> 여기까지가 userRequest의 정보
//        // userRequest 정보를 통해 -> 구글로부터 회원프로필 받아야함(이 때 loadUser 함수 호출하여 받을 수 있음)
//        // loadUser()는 구글로부터 회원 프로필을 받아주는 역할이다.
//        System.out.println("super.loadUser(userRequest).getAttributes() : " + oauth2User.getAttributes());
//
//        // oauth 로그인 정보로 회원가입 자동 진행
//        String provider = userRequest.getClientRegistration().getClientId();
//        String providerId = (String) oauth2User.getAttributes().get("sub");
//        String userName = provider + "_" + providerId;  // ex) google_021348324796
//        String password =bCryptPasswordEncoder.encode("겟인데어");
//        String email = (String) oauth2User.getAttributes().get("email");
//
//        UserEntity user = userRepository.findByNickname(userName).orElseThrow(() -> BusinessException.of(ErrorCode.NOT_FOUND, "해당 유저 정보를 찾을 수 없습니다."));
//        if(user == null){
//            user = UserEntity.builder()
//                    .nickname(userName)
//                    .password(password)
//                    .email(email)
//                    .userRole(UserRole.USER)
//                    .provider(Provider.GOOGLE)
//                    .providerId(providerId)
//                    .refreshToken(null)
//                    .isActive(true)
//                    .build();
//            userRepository.save(user);
//        }
//
//        return new PrincipalDetails(user, oauth2User.getAttributes());
//    }
//}


@Override
public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException{
    System.out.println("userRequest : " + userRequest);
    System.out.println("userRequest.getClientRegistration().getRegistrationId() : " + userRequest.getClientRegistration().getRegistrationId());
    System.out.println("userRequest.getAccessToken().getTokenValue() : " + userRequest.getAccessToken().getTokenValue());

    OAuth2User oauth2User = super.loadUser(userRequest);
    // registrationId : registrationId로 어떤 Oauth로 로그인했는지 확인 가능
    String registrationId = userRequest.getClientRegistration().getRegistrationId();
    // userNameAttributeName : OAuth2 표준에서 Primary key
    String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

    // 1. provider 별로 속성을 정규화
    OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oauth2User.getAttributes());

    // 2. DB에서 사용자 찾기 및 저장 / 업데이트 로직 호출
    UserEntity user = saveOrUpdate(attributes);

    // 3. PrincipalDetails 반환
    return new PrincipalDetails(user, attributes.getAttributes());
    }

    // 사용자 정보 저장 또는 업데이트 메소드
    private UserEntity saveOrUpdate(OAuthAttributes attributes){
        // provider와 providerId를 조합하여 사용자 찾기
        UserEntity user = userRepository.findByProviderAndProviderId(attributes.getProvider(), attributes.getProviderId())
                .map(entity -> entity.update(attributes.getName(), attributes.getEmail()))   // 기존 회원의 닉네임, 이메일 수정
                .orElse(attributes.toEntity());   // 신규 회원 생성

        // userId 생성 로직 추가
        if(user.getUserId() == null){
            user.setUserId(attributes.getProvider().name().toLowerCase() + "_" + attributes.getProviderId());
        }

        // 비밀번호 필드가 not null이므로 더미 값 설정
        if(user.getPassword() == null){
            // 소셜로그인 회원은 비밀번호가 불칠요하지만 DB의 NOT NULL 제약조건 때문에 더미 값 저장
            // Spring Security 5.x 이상에서는 Provider를 통해 인증되므로 실제로 사용되지는 않음.
            user.setPassword(bCryptPasswordEncoder.encode(attributes.getProvider().name().toLowerCase() + "_" + attributes.getProviderId()));
        }

        return userRepository.save(user);
    }
}
