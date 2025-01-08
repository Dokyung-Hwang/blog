package com.post.blog.global.exception.response;

import com.nimbusds.jose.shaded.gson.Gson;
import com.post.blog.global.exception.code.BusinessLogicException;
import com.post.blog.global.exception.code.ExceptionCode;
import com.post.blog.global.response.SingleApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;

import java.io.IOException;

public class ErrorResponder {
    public static void sendErrorResponse(HttpServletResponse response, ExceptionCode exceptionCode) throws IOException {
        Gson gson = new Gson();
        SingleApiResponse<ErrorResponse> errorResponse = SingleApiResponse.fail(ErrorResponse.of(exceptionCode), new BusinessLogicException(exceptionCode));
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(exceptionCode.getStatus());
        response.getWriter().print(gson.toJson(errorResponse, SingleApiResponse.class));

    }
}
