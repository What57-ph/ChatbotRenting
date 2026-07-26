package com.chatbot_renting.commonservice.exception.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Example error codes for the common service itself.
 *
 * <p>Each microservice should define its OWN enum in its own module,
 * implementing {@link AppErrorCode}.  This file serves as a template / reference.
 *
 * Naming convention:
 *   PREFIX-NNN   where PREFIX identifies the service (e.g. CB = Chatbot core,
 *                AUTH = auth service, FILE = file service).
 */
@Getter
@AllArgsConstructor
public enum CommonErrorCode implements AppErrorCode {

  // ---- Generic / shared codes -------------------------------------------
  INTERNAL_SERVER_ERROR("CMN-001", "An unexpected error occurred"),
  RESOURCE_NOT_FOUND("CMN-002", "Requested resource was not found"),
  INVALID_REQUEST("CMN-003", "Request validation failed"),
  UNAUTHORIZED("CMN-004", "Authentication is required"),
  FORBIDDEN("CMN-005", "You do not have permission to perform this action");

  private final String code;
  private final String message;
}
