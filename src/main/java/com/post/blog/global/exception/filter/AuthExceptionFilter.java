package com.post.blog.global.exception.filter;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.post.blog.global.exception.code.BusinessLogicException;
import com.post.blog.global.exception.code.ExceptionCode;
import com.post.blog.global.exception.response.ErrorResponder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// JWT 기반 인증 처리 과정에서 발생하는 예외를 처리하기 위한 필터(HTTP 요청마다 한번 씩 실행되도록 보장)
// 요청을 처리하는 과정에서 특정 예외 발생 시 적절한 에러 응답을 클라이언트에게 반환
@Slf4j
public class AuthExceptionFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);    // JwtAuthenticationProcessingFilter 실행
        } catch (TokenExpiredException e) {     // JWT 토큰 만료된 경우
            log.error("Token expired: {}", e.getMessage());
            ErrorResponder.sendErrorResponse(response, ExceptionCode.EXPIRED_TOKEN);
        } catch (JWTVerificationException e) {      // JWT 서명이 유효하지 않거나 위조된 토큰일 경우
            log.error("JWT verification failed: {}", e.getMessage());
            ErrorResponder.sendErrorResponse(response, ExceptionCode.INVALID_TOKEN);
        } catch (BusinessLogicException e) {        // 비즈니스 로직에서 발생하는 커스텀 예외
            log.error("Business logic: {}", e.getMessage());

            ErrorResponder.sendErrorResponse(response, e.getExceptionCode());
        }
    }
}
