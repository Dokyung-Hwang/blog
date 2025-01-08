package com.post.blog.global.exception.response;


import com.post.blog.global.exception.code.ExceptionCode;
import jakarta.validation.ConstraintViolation;
import lombok.Getter;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

// 유효성 검사 및 예외 발생 시 클라이언트에게 반환할 에러 응답을 구조화하기 위한 DTO 클래스
// 각각의 정적 팩토리 메서드를 통해 다양한 예외 상황을 처리하고 일관된 형식의 에러 메세지 반환
@Getter
public class ErrorResponse {
    private final String field;     // 에러가 발생한 필드 이름
    private final Object invalidValue;      // 잘못된 입력값
    private final String message;

    // 접근제한자를 private 로 두어 외부에서 직접 객체를 생성하지 못화도록 제한
    private ErrorResponse(String field, Object invalidValue, String message) {
        this.field = field;
        this.invalidValue = invalidValue;
        this.message = message;
    }

    // 객체 생성을 위한 정적 팩토리 메서드
    private static ErrorResponse of (String field, Object invalidValue, String message) {
        return new ErrorResponse(field, invalidValue, message);
    }

    // BindingResult 에서 에러 추출(DTO 유효성 검증 실패 시)
    public static List<ErrorResponse> of(final BindingResult bindingResult) {
        return bindingResult.getFieldErrors().stream()
                .map(error -> ErrorResponse.of(
                        error.getField(),
                        error.getRejectedValue() == null ?
                                "" : error.getRejectedValue().toString(), // null이면 "", 아니면 오류난 값
                        error.getDefaultMessage()
                ))
                .collect(Collectors.toList());
    }

    // ConstraintViolation 에서 에러 추출(@PathVariable, @RequestParam 등에서 유효성 검증 실패 시)
    public static List<ErrorResponse> of (final Set<ConstraintViolation<?>> constraintViolations) {
        return constraintViolations.stream()
                .map(constraintViolation -> ErrorResponse.of(
                        constraintViolation.getPropertyPath().toString(),
                        constraintViolation.getInvalidValue().toString(),
                        constraintViolation.getMessage()
                ))
                .collect(Collectors.toList());
    }

    // 필수 요청 파라미터가 누락된 경우
    public static ErrorResponse of(final MissingServletRequestParameterException e) {
        return ErrorResponse.of(e.getParameterName(), null, e.getMessage());
    }

    // 잘못된 타입의 값이 전달된 경우
    public static ErrorResponse of(final MethodArgumentTypeMismatchException e) {
        return ErrorResponse.of(e.getName(), e.getValue(), e.getMessage());
    }

    // 요청 메서드가 지원되지 않는 경우
    public static ErrorResponse of(final HttpRequestMethodNotSupportedException e) {
        return ErrorResponse.of(Objects.requireNonNull(e.getSupportedMethods())[0], e.getMethod(), e.getMessage());
    }

    // 커스텀 예외 코드 처리
    public static ErrorResponse of(final ExceptionCode code) {
        return ErrorResponse.of(null, null, code.getMessage());
    }
}
