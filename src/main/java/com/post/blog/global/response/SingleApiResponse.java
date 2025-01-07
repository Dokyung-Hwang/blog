package com.post.blog.global.response;

import com.post.blog.global.exception.code.BusinessLogicException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class SingleApiResponse<T> extends ApiResponse {
    private final T data;

    private SingleApiResponse(T data, int status, String code, String message) {
        super(status, code, message);
        this.data = data;
    }

    private static <T> SingleApiResponse<T> of(T data, HttpStatus httpStatus) {
        return SingleApiResponse.of(data, httpStatus.value(), httpStatus.name(), httpStatus.getReasonPhrase());
    }

    private static <T> SingleApiResponse<T> of(T data, int status, String code, String message) {
        return new SingleApiResponse<>(data, status, code, message);
    }

    public static <T> SingleApiResponse<T> success(T data) {
        return SingleApiResponse.of(data, HttpStatus.OK);
    }

    public static <T> SingleApiResponse<T> success() {
        return SingleApiResponse.of(null, HttpStatus.OK);
    }

    public static <T> SingleApiResponse<T> fail(T data, BusinessLogicException e) {
        return SingleApiResponse.of(
                data,
                e.getExceptionCode().getStatus(),
                e.getExceptionCode().getCode(),
                e.getExceptionCode().getMessage());
    }
}
