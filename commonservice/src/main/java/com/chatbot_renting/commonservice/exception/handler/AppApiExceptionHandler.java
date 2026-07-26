package com.chatbot_renting.commonservice.exception.handler;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.chatbot_renting.commonservice.exception.AppError;
import com.chatbot_renting.commonservice.exception.AppErrorResponse;
import com.chatbot_renting.commonservice.exception.AppErrorResponse.ErrorDetail;
import com.chatbot_renting.commonservice.exception.AppException;

import lombok.extern.slf4j.Slf4j;

/**
 * Global exception handler for all {@link AppException} subclasses.
 *
 * <p>Registered with {@code @Order(1)} so it takes priority over any default
 * Spring exception handlers. Add this class to the component-scan of each
 * microservice (or configure it via {@code @Import} / auto-configuration) to
 * enable uniform error responses across the platform.
 */
@Slf4j
@Order(1)
@ControllerAdvice
public class AppApiExceptionHandler {

  @ExceptionHandler(AppException.class)
  public ResponseEntity<AppErrorResponse> handleAppException(AppException ex) {

    // Resolve HTTP status from @ResponseStatus on the concrete exception class
    HttpStatus status = Optional
        .ofNullable(AnnotatedElementUtils.findMergedAnnotation(ex.getClass(), ResponseStatus.class))
        .map(ResponseStatus::value)
        .orElse(HttpStatus.INTERNAL_SERVER_ERROR);

    log.error("Application exception [{}]: {}", status, ex.getMessage(), ex);

    // Build error detail list
    List<ErrorDetail> details = ex.getErrors().stream()
        .map(AppApiExceptionHandler::toDetail)
        .collect(Collectors.toList());

    AppErrorResponse body = new AppErrorResponse();
    body.setMessage(ex.getDefaultMessage());
    body.setErrors(details);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    return new ResponseEntity<>(body, headers, status);
  }

  // -------------------------------------------------------------------------
  // Internal helpers
  // -------------------------------------------------------------------------

  private static ErrorDetail toDetail(AppError err) {
    ErrorDetail detail = new ErrorDetail();
    detail.setCode(err.getErrCode().getCode());
    detail.setMessage(err.getErrCode().getMessage());
    detail.setAdditions(err.getAdditions());
    return detail;
  }
}
