package com.post.blog.global.auth.jwt.handler;

import com.post.blog.global.exception.code.ExceptionCode;
import com.post.blog.global.exception.response.ErrorResponder;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

// Custom AccessDeniedHandler
// 권한이 없는 사용자가 보호된 리소스에 접근하려고 할 때 Spring Security 에서 기본적으로 제공하는 오류 페이지가 아닌 커스텀 AccessDeniedHandler
// 403 Forbidden 상태 코드를 클라이언트에게 응답으로 반환하고 커스텀 에러 메세지 제공(Json)
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        ErrorResponder.sendErrorResponse(response, ExceptionCode.ACCOUNT_ACCESS_DENIED);
    }
}
