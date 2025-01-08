package com.post.blog.global.response;

import com.post.blog.global.exception.code.BusinessLogicException;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
public class PaginatedResponse<T> extends ApiResponse {
    private final List<T> data;
    private final PageInfo pageInfo;

    private PaginatedResponse(List<T> data, PageInfo pageInfo, int status, String code, String message) {
        super(status, code, message);
        this.data = data;
        this.pageInfo = pageInfo;
    }

    private static <T> PaginatedResponse<T> of(List<T> data, Page<?> page, HttpStatus httpStatus) {
        return PaginatedResponse.of(data, page, httpStatus.value(), httpStatus.name(), httpStatus.getReasonPhrase());
    }

    private static <T> PaginatedResponse<T> of(List<T> data, Page<?> page, int code, String errorCode, String message) {
        return new PaginatedResponse<>(data, PageInfo.of(page), code, errorCode, message);
    }

    private static <T> PaginatedResponse<T> of(List<T> data, int code, String errorCode, String message) {
        return new PaginatedResponse<>(data, null, code, errorCode, message);
    }

    public static <T> PaginatedResponse<T> success(List<T> data, Page<?> page) {
        return PaginatedResponse.of(data, page, HttpStatus.OK);
    }

    public static PaginatedResponse<Void> fail(BusinessLogicException exception) {
        return PaginatedResponse.of(
                null,
                exception.getExceptionCode().getStatus(),
                exception.getExceptionCode().name(),
                exception.getExceptionCode().getMessage()
        );
    }
}
