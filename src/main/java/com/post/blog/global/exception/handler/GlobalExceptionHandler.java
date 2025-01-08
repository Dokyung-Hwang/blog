package com.post.blog.global.exception.handler;

import com.post.blog.global.exception.code.BusinessLogicException;
import com.post.blog.global.exception.code.ExceptionCode;
import com.post.blog.global.exception.response.ErrorResponse;
import com.post.blog.global.response.ListResponse;
import com.post.blog.global.response.SingleApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

// 전역 예외 처리
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    // @Valid로 @RequestBody에 매핑되는 dto를 검증할 때 발생
    public ResponseEntity<ListResponse<ErrorResponse>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e,
                                                                                             HttpServletRequest httpServletRequest) {
        ListResponse<ErrorResponse> responses =
                ListResponse.fail(ErrorResponse.of(e.getBindingResult()), new BusinessLogicException(ExceptionCode.INVALID_INPUT_VALUE));
        return new ResponseEntity<>(responses, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    // @PathVariable이나 @RequestParam에 매핑되는 경로, 쿼리 파라미터를 검증할 때 발생
    public ResponseEntity<ListResponse<ErrorResponse>> handleConstraintViolationException(ConstraintViolationException e,
                                                                                           HttpServletRequest httpServletRequest) {
        ListResponse<ErrorResponse> responses =
                ListResponse.fail(ErrorResponse.of(e.getConstraintViolations()), new BusinessLogicException(ExceptionCode.INVALID_INPUT_VALUE));
        return new ResponseEntity<>(responses, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    // @RequestParam에 매핑되는 쿼리 파라미터가 존재하지 않을 때 발생
    public ResponseEntity<SingleApiResponse<ErrorResponse>> handleMissingServletRequestParameterException(MissingServletRequestParameterException e,
                                                                                                          HttpServletRequest httpServletRequest) {
        SingleApiResponse<ErrorResponse> response =
                SingleApiResponse.fail(ErrorResponse.of(e), new BusinessLogicException(ExceptionCode.PARAMETER_NOT_FOUND));
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
    // @PathVariable이나 @RequestParam에 매핑되는 경로, 쿼리 파라미터의 타입이 일치하지 않을 때 발생
    public ResponseEntity<SingleApiResponse<ErrorResponse>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e,
                                                                                                      HttpServletRequest httpServletRequest) {
        SingleApiResponse<ErrorResponse> response =
                SingleApiResponse.fail(ErrorResponse.of(e), new BusinessLogicException(ExceptionCode.INVALID_INPUT_TYPE));
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    // request의 http method가 다를 때 발생
    public ResponseEntity<SingleApiResponse<ErrorResponse>> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e,
                                                                                                         HttpServletRequest httpServletRequest) {
        SingleApiResponse<ErrorResponse> response =
                SingleApiResponse.fail(ErrorResponse.of(e), new BusinessLogicException(ExceptionCode.METHOD_NOT_ALLOWED));
        return new ResponseEntity<>(response, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(BusinessLogicException.class)
    public ResponseEntity<SingleApiResponse<ErrorResponse>> handleBusinessLogicException(BusinessLogicException e,
                                                                                         HttpServletRequest httpServletRequest) {
        SingleApiResponse<ErrorResponse> response =
                SingleApiResponse.fail(null, e);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(e.getExceptionCode().getStatus()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<SingleApiResponse<ErrorResponse>> handleException(Exception e, HttpServletRequest httpServletRequest) {
        log.error("Internal Server Error: {}", e.getClass());
        log.error("{}", e.getMessage());
        SingleApiResponse<ErrorResponse> response =
                SingleApiResponse.fail(ErrorResponse.of(ExceptionCode.INTERNAL_SERVER_ERROR), new BusinessLogicException(ExceptionCode.INTERNAL_SERVER_ERROR));

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
