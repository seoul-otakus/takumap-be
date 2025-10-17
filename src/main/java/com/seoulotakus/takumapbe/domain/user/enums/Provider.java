package com.seoulotakus.takumapbe.domain.user.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Provider {
    
    LOCAL("자체 로그인"),
    GOOGLE("구글"),
    KAKAO("카카오"),
    NAVER("네이버");
    
    private final String provider;
}
