package com.seoulotakus.takumapbe.common.auth.service;

import com.seoulotakus.takumapbe.common.auth.dto.request.*;
import com.seoulotakus.takumapbe.common.auth.dto.response.AccessTokenResponseDTO;
import com.seoulotakus.takumapbe.common.auth.dto.response.LoginResponseDTO;
import com.seoulotakus.takumapbe.common.auth.entity.CertificationEntity;
import com.seoulotakus.takumapbe.common.auth.provider.jwt.JwtProvider;
import com.seoulotakus.takumapbe.common.auth.provider.mail.EmailProvider;
import com.seoulotakus.takumapbe.common.auth.repository.CertificationRepository;
import com.seoulotakus.takumapbe.domain.user.entity.UserEntity;
import com.seoulotakus.takumapbe.domain.user.enums.Provider;
import com.seoulotakus.takumapbe.domain.user.enums.UserRole;
import com.seoulotakus.takumapbe.domain.user.repository.UserRepository;
import com.seoulotakus.takumapbe.global.exception.BusinessException;
import com.seoulotakus.takumapbe.global.exception.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImplement implements AuthService {

    private final UserRepository userRepository;
    private final CertificationRepository certificationRepository;
    private final JwtProvider jwtProvider;
    private final EmailProvider emailProvider;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void idCheck(IdCheckRequestDTO requestDTO) {
        String userId = requestDTO.getUserId();

        boolean isExistId = userRepository.existsByUserId(userId);
        if(isExistId) {
            throw new BusinessException(ErrorCode.DUPLICATE_ID);
        }
    }

    @Override
    public void nicknameCheck(NicknameCheckRequestDTO requestDTO) {
        String nickname = requestDTO.getNickname();

        boolean isExistNickname = userRepository.existsByNickname(nickname);

        if(isExistNickname) {
            throw new BusinessException(ErrorCode.DUPLICATE_NICKNAME);
        }
    }

    @Override
    public void certificateEmail(EmailCertificationRequestDTO emailRequestDTO) {

        String userId = emailRequestDTO.getUserId();
        String email = emailRequestDTO.getEmail();

        boolean isExistId = userRepository.existsByUserId(userId);
        if(isExistId) {
            throw new BusinessException(ErrorCode.DUPLICATE_ID);
        }

        String certificationNumber = getCertificationNumber();

        boolean isSuccessed = emailProvider.sendCertificationMail(email, certificationNumber);

        if(!isSuccessed){
            throw new BusinessException(ErrorCode.MAIL_FAIL);
        }

        CertificationEntity certificationEntity = CertificationEntity.builder()
                .userId(userId)
                .email(email)
                .certificationNumber(certificationNumber)
                .build();
        certificationRepository.save(certificationEntity);
    }

    private String getCertificationNumber(){

        String certificationNumber = "";

        for(int count = 0; count < 6; count++){
            certificationNumber += (int) (Math.random() * 10);
        }

        return certificationNumber;
    }

    @Override
    public void checkCertification(CheckCertificationRequestDTO certificationRequestDTO) {

        String userId = certificationRequestDTO.getUserId();
        String email = certificationRequestDTO.getEmail();
        String certificationNumber = certificationRequestDTO.getCertificationNumber();

        CertificationEntity certificationEntity = certificationRepository.findByUserId(userId);

        if(certificationEntity == null){
            throw new BusinessException(ErrorCode.NOT_FOUND);
        }

        boolean isMatched = certificationEntity.getEmail().equals(email) && certificationEntity.getCertificationNumber().equals(certificationNumber);

        if(!isMatched){
            throw new BusinessException(ErrorCode.NOT_FOUND);
        }
    }

    @Override
    public void signUp(SignUpRequestDTO signUpRequestDTO) {

        // userId 중복 체크
        String userId = signUpRequestDTO.getUserId();
        boolean isExistId = userRepository.existsByUserId(userId);
        if(isExistId){
            throw new BusinessException(ErrorCode.DUPLICATE_ID);
        }

        // 닉네임 중복 체크
        String nickname = signUpRequestDTO.getNickname();
        boolean isExistsNickname = userRepository.existsByNickname(nickname);
        if(isExistsNickname){
            throw new BusinessException(ErrorCode.DUPLICATE_NICKNAME);
        }

        // 이메일 중복 체크
        String email = signUpRequestDTO.getEmail();
        boolean isExistsEmail = userRepository.existsByEmail(email);
        if(isExistsEmail){
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }

        // 이메일 인증 코드 일치 확인
        String certificaionNumber = signUpRequestDTO.getCertificationNumber();

        CertificationEntity certificationEntity = certificationRepository.findByUserId(userId);
        boolean isMatched = certificationEntity.getEmail().equals(email) && certificationEntity.getCertificationNumber().equals(certificaionNumber);
        if(!isMatched){
            throw new BusinessException(ErrorCode.CERTIFICATION_FAIL);
        }

        // 비밀번호 인코딩
        String password = signUpRequestDTO.getPassword();
        String encodedPassword = passwordEncoder.encode(password);

        // user 저장
        UserEntity userEntity = UserEntity.builder()
                .userId(signUpRequestDTO.getUserId())
                .password(encodedPassword)
                .nickname(signUpRequestDTO.getNickname())
                .email(signUpRequestDTO.getEmail())
                .userRole(UserRole.ROLE_USER)
                .provider(Provider.LOCAL)
                .isActive(true)
                .build();

        userRepository.save(userEntity);
        certificationRepository.deleteByUserId(signUpRequestDTO.getUserId());
    }

    @Override
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {

        String token = null;
        String refreshToken = null;

        // 아이디 일치 확인
        String userId = loginRequestDTO.getUserId();
        UserEntity userEntity = userRepository.findByUserId(userId).orElseThrow(() -> BusinessException.of(ErrorCode.LOGIN_FAIL, "해당 아이디는 존재하지 않은 아이디입니다."));

        Long id = userEntity.getId();
        String nickname = userEntity.getNickname();
        UserRole userRole = userEntity.getUserRole();
        boolean isActive = userEntity.getIsActive();

        // 비밀번호 일치 확인
        String password = loginRequestDTO.getPassword();
        String encodedPwd = userEntity.getPassword();
        boolean isMatched = passwordEncoder.matches(password, encodedPwd);
        if(!isMatched){
            throw BusinessException.of(ErrorCode.LOGIN_FAIL, "해당 비밀번호는 존재하지 않는 비밀번호입니다.");
        }

        // token 생성
        token = jwtProvider.createAccessToken(userId);

        // refreshToken 생성
        refreshToken = jwtProvider.createRefreshToken(userId);

        // refreshToken 저장
        userRepository.updateRefreshToken(refreshToken, id);

        LoginResponseDTO newLoginResponse = new LoginResponseDTO(nickname, userRole, isActive, token, refreshToken);

        return newLoginResponse;
    }

    @Override
    public AccessTokenResponseDTO refreshAccessToken(String refreshToken){
        String userId = jwtProvider.validate(refreshToken);
        if (userId == null){
            System.out.println("======= INVALID_REFRESH_TOKEN =======");
            throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        UserEntity user = userRepository.findByUserId(userId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if(!refreshToken.equals(user.getRefreshToken())){
            System.out.println("+++++ INVALID_REFRESH_TOKEN +++++");
            throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        String newAccessToken = jwtProvider.createAccessToken(userId);
        return new AccessTokenResponseDTO(newAccessToken);
    }

    @Override
    public void logout(String refreshToken){
        String userId = jwtProvider.validate(refreshToken);
        if(userId == null){
            throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        UserEntity user = userRepository.findByUserId(userId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 데이터베이스에서 refresh token 무효화
        user.setRefreshToken(null);
        userRepository.save(user);
    }

}
