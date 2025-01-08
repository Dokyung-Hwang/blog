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
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class AuthExceptionFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);    // JwtAuthenticationProcessingFilter 실행
        } catch (TokenExpiredException e) {
            ErrorResponder.sendErrorResponse(response, ExceptionCode.EXPIRED_TOKEN);
        } catch (JWTVerificationException e) {
            ErrorResponder.sendErrorResponse(response, ExceptionCode.INVALID_TOKEN);
        } catch (BusinessLogicException e) {
            ErrorResponder.sendErrorResponse(response, e.getExceptionCode());
        }
    }
}
