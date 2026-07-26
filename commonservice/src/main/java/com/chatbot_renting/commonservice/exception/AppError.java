package com.chatbot_renting.commonservice.exception;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.chatbot_renting.commonservice.exception.code.AppErrorCode;

import lombok.Getter;

/**
 * Wraps a single error code with optional extra context (i18n args, free-text additions).
 *
 * <pre>
 * // Minimal — just an error code
 * new AppError(CoreErrorCode.CHATBOT_NOT_FOUND)
 *
 * // With formatted message args (used for i18n interpolation)
 * new AppError(CoreErrorCode.CHATBOT_NOT_FOUND, null, chatbotId)
 *
 * // With extra free-text additions
 * new AppError(CoreErrorCode.CHATBOT_NOT_FOUND, "Resource ID: " + chatbotId)
 * </pre>
 */
@Getter
public class AppError {

  /** The typed error code. */
  private final AppErrorCode errCode;

  /**
   * Optional free-text context appended to the error response
   * (e.g. a field name or resource identifier).
   */
  private final String additions;

  /** Positional arguments used for i18n message interpolation. */
  private final List<Object> args;

  public AppError(AppErrorCode errCode) {
    this.errCode = errCode;
    this.additions = null;
    this.args = new ArrayList<>();
  }

  public AppError(AppErrorCode errCode, String additions, Object... args) {
    this.errCode = errCode;
    this.additions = additions;
    this.args = Arrays.asList(args);
  }
}
