package com.post.blog.global.exception;

import lombok.Getter;

@Getter
public enum ExceptionCode {
    ACCOUNT_UNAUTHORIZED(401, "Account unauthorized"),
    ACCOUNT_NOT_FOUND(404, "Account not found"),
    ACCOUNT_NOT_ALLOW(405, "That Account doesn't have authority"),
    ACCOUNT_ALREADY_EXISTS(409, "Account already exists"),

    POST_NOT_FOUND(404, "Post not found"),
    POST_NOT_ALLOW(405, "That Post doesn't have authority"),
    POST_ALREADY_EXISTS(409, "Post already exists");

    private final int code;
    private final String message;

    ExceptionCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
