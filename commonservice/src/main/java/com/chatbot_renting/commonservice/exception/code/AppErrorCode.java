package com.chatbot_renting.commonservice.exception.code;

/**
 * Marker interface for all application error codes.
 * Each microservice should define its own enum implementing this interface.
 *
 * Example:
 * <pre>
 * {@code
 * @Getter
 * @AllArgsConstructor
 * public enum CoreErrorCode implements AppErrorCode {
 *   CHATBOT_NOT_FOUND("CB-001", "Chatbot not found"),
 *   CHATBOT_NAME_EXISTS("CB-002", "Chatbot name already exists");
 *
 *   private final String code;
 *   private final String message;
 * }
 * }
 * </pre>
 */
public interface AppErrorCode {

  /** Short, machine-readable error code (e.g. "CB-001"). */
  String getCode();

  /** Human-readable default message. */
  String getMessage();
}
