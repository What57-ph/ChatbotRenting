package com.chatbot_renting.commonservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.annotation.Nonnull;

/**
 * Thrown when an unexpected server-side failure occurs.
 * Maps to HTTP 500 Internal Server Error.
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"message", "key", "errors"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppInternalServerErrorException extends AppException {

  private static final long serialVersionUID = 1L;

  public AppInternalServerErrorException(@Nonnull AppError... errors) {
    super(errors);
  }

  @Override
  public String getDefaultMessage() {
    return "Internal Server Error";
  }
}
