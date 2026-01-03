//package com.seoulotakus.takumapbe.global.util;
//
//import com.seoulotakus.takumapbe.domain.user.entity.UserEntity;
//import com.seoulotakus.takumapbe.domain.user.enums.Provider;
//import com.seoulotakus.takumapbe.domain.user.enums.UserRole;
//import com.seoulotakus.takumapbe.domain.user.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.core.MethodParameter;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.web.bind.support.WebDataBinderFactory;
//import org.springframework.web.context.request.NativeWebRequest;
//import org.springframework.web.method.support.HandlerMethodArgumentResolver;
//import org.springframework.web.method.support.ModelAndViewContainer;
//
///**
// * @CurrentUser 어노테이션을 처리하는 ArgumentResolver
// */
//@Slf4j
//@RequiredArgsConstructor
//public class UserArgumentResolver implements HandlerMethodArgumentResolver {
//
//    private final UserRepository userRepository;
//    private static final Long MOCK_USER_ID = 1L; // 스텁용 유저 ID
//
//    @Override
//    public boolean supportsParameter(MethodParameter parameter) {
//        // @CurrentUser 어노테이션이 붙어있고, 파라미터 타입이 User 인 경우
//        return parameter.getParameterAnnotation(CurrentUser.class) != null
//                && parameter.getParameterType().equals(UserEntity.class);
//    }
//
//    @Override
//    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
//                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
//
//        Object principal = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//
//        if (principal == null || "anonymousUser".equals(principal)) {
//            return null;
//        }
//
//        String userId = (String) principal;
//        return userRepository.findByUserId(userId)
//                .orElseThrow(() -> new com.seoulotakus.takumapbe.global.exception.BusinessException(com.seoulotakus.takumapbe.global.exception.ErrorCode.USER_NOT_FOUND));
//    }
//}

package com.seoulotakus.takumapbe.global.util;

import com.seoulotakus.takumapbe.domain.user.entity.UserEntity;
import com.seoulotakus.takumapbe.domain.user.enums.Provider;
import com.seoulotakus.takumapbe.domain.user.enums.UserRole;
import com.seoulotakus.takumapbe.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/** @CurrentUser 어노테이션을 처리하는 ArgumentResolver **/
@Slf4j
@RequiredArgsConstructor
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    private final UserRepository userRepository;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // @CurrentUser 어노테이션이 붙어있고, 파라미터 타입이 User 인 경우
        return parameter.getParameterAnnotation(CurrentUser.class) != null
                && parameter.getParameterType().equals(UserEntity.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        Object principal = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal == null || "anonymousUser".equals(principal)) {
            return null;
        }

        String userId = (String) principal;
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new com.seoulotakus.takumapbe.global.exception.BusinessException(com.seoulotakus.takumapbe.global.exception.ErrorCode.USER_NOT_FOUND));
    }
}
