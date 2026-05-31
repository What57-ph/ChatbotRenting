package com.lecturemind.commonservice.exception;


import com.lecturemind.commonservice.domain.Response.ApiResponse;
import com.lecturemind.commonservice.domain.Response.ResponseBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandle {
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<?>> handleRuntime(RuntimeException e){
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseBuilder.error(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        e.getMessage()
                ));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<?>> handleUnauthorized(UnauthorizedException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ResponseBuilder.error(HttpStatus.UNAUTHORIZED, ex.getMessage()));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleNotFound(NotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ResponseBuilder.error(HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    @ExceptionHandler(ExistException.class)
    public ResponseEntity<ApiResponse<?>> handleExist(ExistException ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseBuilder.error(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage()));
    }

}
