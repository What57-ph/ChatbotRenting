package com.chatbot_renting.commonservice.exception;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.annotation.Nonnull;
import lombok.Getter;

/**
 * Base class for all domain exceptions in the Chatbot Renting platform.
 *
 * <p>Subclasses are annotated with {@code @ResponseStatus} to declare the HTTP status code.
 * The {@link #getDefaultMessage()} method returns a user-friendly message used in the response body.
 *
 * <p>Usage example:
 * <pre>
 * throw new AppNotFoundException(
 *     new AppError(CoreErrorCode.CHATBOT_NOT_FOUND, "id: " + id)
 * );
 * </pre>
 */
@Getter
public abstract class AppException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  /** List of structured errors carried by this exception. */
  protected final List<AppError> errors;

  protected AppException(@Nonnull AppError... errors) {
    super(buildMessage(errors));
    this.errors = Arrays.asList(errors);
  }

  /**
   * A short, user-facing message that describes the overall failure category.
   * Returned as the {@code message} field in the JSON error response.
   */
  public abstract String getDefaultMessage();

  // -------------------------------------------------------------------------
  // Helpers
  // -------------------------------------------------------------------------

  private static String buildMessage(AppError[] errors) {
    return Arrays.stream(errors)
        .map(e -> e.getErrCode().getCode() + " - " + e.getErrCode().getMessage())
        .collect(Collectors.joining(", "));
  }
}
