package com.seoulotakus.takumapbe.global.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    // ========== HTTP 표준 상태 코드 ==========
    INVALID_REQUEST("유효하지 않은 요청입니다", 400, HttpStatus.BAD_REQUEST),
    UNAUTHORIZED("인증 정보가 없습니다", 401, HttpStatus.UNAUTHORIZED),
    FORBIDDEN("접근 권한이 없습니다", 403, HttpStatus.FORBIDDEN),
    INTERNAL_SERVER_ERROR("서버 오류가 발생했습니다", 500, HttpStatus.INTERNAL_SERVER_ERROR),
    NOT_FOUND("해당 정보가 존재하지 앖습니다.", 404, HttpStatus.NOT_FOUND),
    DUPLICATE_ID("해당 아이디는 중복된 아이디입니다.", 405, HttpStatus.BAD_REQUEST),
    MAIL_FAIL("이메일 발송에 실패했습니다.", 406, HttpStatus.INTERNAL_SERVER_ERROR),
    CERTIFICATION_FAIL("이메일 인증 코드가 일치하지 않습니다.", 407, HttpStatus.BAD_REQUEST),
    DUPLICATE_NICKNAME("해당 닉네임은 중복된 닉네임입니다.", 408, HttpStatus.BAD_REQUEST),
    DUPLICATE_EMAIL("해당 이메일은 중복된 이메일입니다.", 409, HttpStatus.BAD_REQUEST),
    LOGIN_FAIL("로그인에 실패했습니다.", 410, HttpStatus.BAD_REQUEST),
    INVALID_REFRESH_TOKEN("잘못된 Refresh Token입니다.", 411, HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND("해당 유저는 존재하지 않습니다.", 412, HttpStatus.NOT_FOUND),
    EMAIL_NOT_FOUND("해당 이메일은 존재하지 않습니다.", 413, HttpStatus.NOT_FOUND),
    CERTIFICATION_PASSWORD_FAIL("임시 비밀번호가 일치하지 않습니다.", 414, HttpStatus.BAD_REQUEST);

    private final String message;
    private final int statusCode;
    private final HttpStatus status;

    ErrorCode(String message, int statusCode, HttpStatus status) {
        this.message = message;
        this.statusCode = statusCode;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
