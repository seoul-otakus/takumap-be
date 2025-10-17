package com.seoulotakus.takumapbe.domain.user.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum UserRole {

    USER("회원"),
    ADMIN("관리자");

    private final String userRole;
}
