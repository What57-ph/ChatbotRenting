package com.chatbot_renting.commonservice.exception;

import jakarta.annotation.Nonnull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when a request is not authenticated (missing or invalid JWT token).
 * Maps to HTTP 401 Unauthorized.
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class AppUnauthorizedException extends AppException {

    private static final long serialVersionUID = 1L;

    public AppUnauthorizedException(@Nonnull AppError... errors) {
        super(errors);
    }

    @Override
    public String getDefaultMessage() {
        return "Unauthorized";
    }
}
