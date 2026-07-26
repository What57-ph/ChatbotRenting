package com.chatbot_renting.commonservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.annotation.Nonnull;

/**
 * Thrown when a requested resource does not exist.
 * Maps to HTTP 404 Not Found.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"message", "key", "errors"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppNotFoundException extends AppException {

  private static final long serialVersionUID = 1L;

  public AppNotFoundException(@Nonnull AppError... errors) {
    super(errors);
  }

  @Override
  public String getDefaultMessage() {
    return "Not Found";
  }
}
