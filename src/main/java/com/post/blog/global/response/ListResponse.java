package com.post.blog.global.response;

import com.post.blog.global.exception.code.BusinessLogicException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
public class ListResponse<T> extends ApiResponse {
    private final List<T> data;

    private ListResponse(List<T> data, int status, String code, String message) {
        super(status, code, message);
        this.data = data;
    }

    private static <T> ListResponse<T> of(List<T> data, HttpStatus httpStatus) {
        return ListResponse.of(data, httpStatus.value(), httpStatus.name(), httpStatus.getReasonPhrase());
    }

    private static <T> ListResponse<T> of(List<T> data, int status, String code, String message) {
        return new ListResponse<>(data, status, code, message);
    }

    public static <T> ListResponse<T> success(List<T> data) {
        return ListResponse.of(data, HttpStatus.OK);
    }

    public static <T> ListResponse<T> success() {
        return ListResponse.of(null, HttpStatus.OK);
    }

    public static <T> ListResponse<T> fail(List<T> data, BusinessLogicException e) {
        return new ListResponse<>(
                data,
                e.getExceptionCode().getStatus(),
                e.getExceptionCode().getCode(),
                e.getExceptionCode().getMessage());
    }
}
