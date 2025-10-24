package com.seoulotakus.takumapbe.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private HttpStatus status;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer errorCode;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ErrorDetail errorData;

    private String message;
    private T data;

    // 성공 응답 메소드
    public static <T> ApiResponse<T> success(T data){
        return ApiResponse.<T>builder()
                .status(HttpStatus.OK)
                .errorCode(null)
                .errorData(null)
                .message("요청이 성공적으로 처리되었습니다.")
                .data(data)
                .build();
    }

    // 성공 응답 메소드 - 메세지 커스텀
    public static <T> ApiResponse<T> success(T data, String message){
        return ApiResponse.<T>builder()
                .status(HttpStatus.OK)
                .errorCode(null)
                .errorData(null)
                .message(message)
                .data(data)
                .build();
    }

    // 실패 응답 생성 메서드 (기본)
    public static <T> ApiResponse<T> fail(HttpStatus httpStatus, Integer errorCode, String message) {
        return ApiResponse.<T>builder()
                .status(httpStatus)
                .errorCode(errorCode)
                .errorData(null)
                .message(message)
                .data(null)
                .build();
    }

    // 실패 응답 메소드 (상세 에러 코드 추가)
    public static <T> ApiResponse<T> fail(HttpStatus httpStatus, Integer errorCode, String message, String errorSpot, String errorMessage){
        ErrorDetail errorDetail = ErrorDetail.builder()
                .errorSpot(errorSpot)
                .errorMessage(errorMessage)
                .build();

        return ApiResponse.<T>builder()
                .status(httpStatus)
                .errorCode(errorCode)
                .errorData(errorDetail)
                .message(message)
                .data(null)
                .build();
    }
}
