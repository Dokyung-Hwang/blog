package com.post.blog.global.exception.code;

import lombok.Getter;

@Getter
public enum ExceptionCode {
    INVALID_INPUT_VALUE(400, "COMMON-001", "Invalid Input Value"),
    PARAMETER_NOT_FOUND(400, "COMMON-002", "Parameter Not Found"),
    INVALID_INPUT_TYPE(400, "COMMON-003", "Invalid Input Type"),
    METHOD_NOT_ALLOWED(405, "COMMON-004", "Method Not Allowed"),
    INTERNAL_SERVER_ERROR(500, "COMMON-005", "Server Error"),

    LOGIN_FAILED(400, "AUTH-001", "Login Failed"),
    OAUTH2_LOGIN_FAILED(400, "AUTH-002", "OAuth2 Login Failed"),
    EXPIRED_TOKEN(401, "AUTH-003", "Expired Token"),
    INVALID_TOKEN(401, "AUTH-004", "Invalid Token"),
    ACCOUNT_UNAUTHORIZED(401, "AUTH-005", "Account Unauthorized"),
    ACCOUNT_ACCESS_DENIED(403, "AUTH-006", "Access is Denied"),

    ACCOUNT_NOT_FOUND(404, "ACCOUNT-001", "Account not found"),
    ACCOUNT_NOT_ALLOW(405, "ACCOUNT-002", "That Account doesn't have authority"),
    ACCOUNT_ALREADY_EXISTS(409, "ACCOUNT-003", "Account already exists"),
    EMAIL_ALREADY_EXISTS(409, "ACCOUNT-004", "Email already exists"),
    NICKNAME_ALREADY_EXISTS(409, "ACCOUNT-005", "Nickname already exists"),
    PASSWORD_NOT_VALID(400, "ACCOUNT-006", "Password Not Valid"),

    POST_NOT_FOUND(404, "POST-001","Post not found"),
    POST_NOT_ALLOW(405, "POST-002","That Post doesn't have authority"),
    POST_ALREADY_EXISTS(409, "POST-003","Post already exists"),

    COMMENT_NOT_FOUND(404, "COMMENT-001", "Comment Not Found"),
    COMMENT_NOT_ALLOW(405, "COMMENT-002", "That Comment doesn't have authority"),
    COMMENT_ALREADY_EXISTS(409, "COMMENT-003", "Comment Already Exists");

    private final int status;
    private final String code;
    private final String message;

    ExceptionCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
