//package com.seoulotakus.takumapbe.domain.auth.exception;
//
//import com.seoulotakus.takumapbe.global.exception.ErrorCode;
//import com.seoulotakus.takumapbe.global.response.ApiResponse;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.converter.HttpMessageNotReadableException;
//import org.springframework.web.bind.MethodArgumentNotValidException;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//
//@RestControllerAdvice
//public class ValidationExceptionHandler {
//
//    @ExceptionHandler({MethodArgumentNotValidException.class, HttpMessageNotReadableException.class})
//    public ApiResponse<?> validatationExceptionHandler(Exception exception){
//        return ApiResponse.fail(HttpStatus.UNAUTHORIZED, 401, "유효성 검사에 실패했습니다.");
//    }
//}
