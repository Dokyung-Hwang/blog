package com.post.blog.global.response;

import com.post.blog.global.exception.code.BusinessLogicException;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
public class PageResponse<T> extends ApiResponse {
    private final List<T> data;
    private final PageInfo pageInfo;

    private PageResponse(List<T> data, PageInfo pageInfo, int status, String code, String message) {
        super(status, code, message);
        this.data = data;
        this.pageInfo = pageInfo;
    }

    private static <T> PageResponse<T> of(List<T> data, Page<?> page, HttpStatus httpStatus) {
        return PageResponse.of(data, page, httpStatus.value(), httpStatus.name(), httpStatus.getReasonPhrase());
    }

    private static <T> PageResponse<T> of(List<T> data, Page<?> page, int code, String errorCode, String message) {
        return new PageResponse<>(data, PageInfo.of(page), code, errorCode, message);
    }

    private static <T> PageResponse<T> of(List<T> data, int code, String errorCode, String message) {
        return new PageResponse<>(data, null, code, errorCode, message);
    }

    public static <T> PageResponse<T> success(List<T> data, Page<?> page) {
        return PageResponse.of(data, page, HttpStatus.OK);
    }

    public static PageResponse<Void> fail(BusinessLogicException exception) {
        return PageResponse.of(
                null,
                exception.getExceptionCode().getStatus(),
                exception.getExceptionCode().name(),
                exception.getExceptionCode().getMessage()
        );
    }
}
