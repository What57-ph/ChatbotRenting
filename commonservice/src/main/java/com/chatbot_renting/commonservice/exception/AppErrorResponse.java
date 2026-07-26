package com.chatbot_renting.commonservice.exception;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Standard error response body returned by {@link AppApiExceptionHandler}.
 *
 * <pre>
 * {
 *   "message": "Bad Request",
 *   "errors": [
 *     {
 *       "code": "CB-001",
 *       "message": "Chatbot not found",
 *       "additions": "id: 42"
 *     }
 *   ]
 * }
 * </pre>
 */
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"message", "errors"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppErrorResponse {

  /** Top-level message describing the error category (e.g. "Bad Request"). */
  @JsonProperty("message")
  private String message;

  /** List of individual error details. */
  @JsonProperty("errors")
  private List<ErrorDetail> errors;

  // -----------------------------------------------------------------------
  // Nested DTO
  // -----------------------------------------------------------------------

  /**
   * Represents one specific error within the response.
   */
  @Data
  @NoArgsConstructor
  @JsonInclude(JsonInclude.Include.NON_NULL)
  public static class ErrorDetail {

    /** Machine-readable error code (e.g. "CB-001"). */
    @JsonProperty("code")
    private String code;

    /** Human-readable error message. */
    @JsonProperty("message")
    private String message;

    /**
     * Optional free-text context (field name, resource ID, etc.).
     * Omitted from the response when {@code null}.
     */
    @JsonProperty("additions")
    private String additions;

    /**
     * Optional i18n translations keyed by locale tag (e.g. {@code "vi"}, {@code "en"}).
     * Omitted from the response when empty.
     */
    @JsonProperty("i18n")
    private Map<String, String> i18n;
  }
}
