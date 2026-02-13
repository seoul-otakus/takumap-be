package com.seoulotakus.takumapbe.global.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.util.Enumeration;

@Component
@Slf4j
public class RequestLoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        // HttpServletRequest를 ContentCachingRequestWrapper로 래핑합니다.
        // 이렇게 하면 요청 본문을 여러 번 읽을 수 있습니다.
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper((HttpServletRequest) request);

        // 다음 필터로 체인을 계속 진행합니다.
        // 이 부분이 먼저 호출되어야 컨트롤러 로직이 실행됩니다.
        chain.doFilter(requestWrapper, response);

        // 특정 경로에 대해서만 로깅을 수행합니다.
        String requestURI = requestWrapper.getRequestURI();
        if (requestURI.startsWith("/api/v1/auth/")) {
            log.info("========== API REQUEST LOG START (URI: {}) ==========", requestURI);
            log.info("Request Method: {}", requestWrapper.getMethod());

            // 헤더 정보 로깅
            Enumeration<String> headerNames = requestWrapper.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                log.info("Header [{}]: {}", headerName, requestWrapper.getHeader(headerName));
            }

            // 요청 본문(Body) 로깅
            byte[] content = requestWrapper.getContentAsByteArray();
            if (content.length > 0) {
                String requestBody = new String(content, requestWrapper.getCharacterEncoding());
                log.info("Request Body: {}", requestBody);
            } else {
                log.info("Request Body: [EMPTY]");
            }
            log.info("=========== API REQUEST LOG END (URI: {}) ===========", requestURI);
        }
    }
}
