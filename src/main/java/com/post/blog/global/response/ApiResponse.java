package com.post.blog.global.response;

import com.post.blog.global.audit.BaseTimeEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ApiResponse {
    private final int status;
    private final String code;
    private final String message;

    @Builder
    public ApiResponse(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
