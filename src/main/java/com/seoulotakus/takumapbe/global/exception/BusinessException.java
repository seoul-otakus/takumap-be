package com.seoulotakus.takumapbe.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode, Throwable cause){
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }

    public BusinessException(ErrorCode errorCode, String message){
        super(message);
        this.errorCode = errorCode;
    }

    public BusinessException(ErrorCode errorCode, String message, Throwable cause){
        super(message, cause);
        this.errorCode = errorCode;
    }

    public static BusinessException of(ErrorCode errorCode){
        return new BusinessException(errorCode);
    }

    public static BusinessException of(ErrorCode errorCode, String message){
        return new BusinessException(errorCode, message);
    }

    public static BusinessException of(ErrorCode errorCode, Throwable cause){
        return new BusinessException(errorCode, cause);
    }

    public static BusinessException of(ErrorCode errorCode, String message, Throwable cause){
        return new BusinessException(errorCode, message, cause);
    }

    public ErrorCode getErrorCode(){
        return errorCode;
    }

}
