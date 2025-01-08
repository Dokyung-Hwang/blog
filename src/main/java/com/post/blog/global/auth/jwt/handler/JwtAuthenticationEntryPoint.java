package com.post.blog.global.auth.jwt.handler;

import com.post.blog.global.exception.code.ExceptionCode;
import com.post.blog.global.exception.response.ErrorResponder;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        ErrorResponder.sendErrorResponse(response, ExceptionCode.ACCOUNT_UNAUTHORIZED);
    }
}
