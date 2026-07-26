package com.chatbot_renting.commonservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.annotation.Nonnull;

/**
 * Thrown when an operation cannot proceed due to invalid client input.
 * Maps to HTTP 400 Bad Request.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"message", "key", "errors"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppBadRequestException extends AppException {

  private static final long serialVersionUID = 1L;

  public AppBadRequestException(@Nonnull AppError... errors) {
    super(errors);
  }

  @Override
  public String getDefaultMessage() {
    return "Bad Request";
  }
}
