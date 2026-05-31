package com.lecturemind.commonservice.domain.Response;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class ResponseBuilder {
    public static <T> ApiResponse<T> success(T data){
        return ApiResponse.<T>builder()
                .status(HttpStatus.OK.value())
                .message("Success")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }
    public static <T> ApiResponse<T> created(T data){
        return ApiResponse.<T>builder()
                .status(HttpStatus.CREATED.value())
                .message("Created")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> created(T data, String message){
        return ApiResponse.<T>builder()
                .status(HttpStatus.CREATED.value())
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> error(HttpStatus status, String message){
        return ApiResponse.<T>builder()
                .status(status.value())
                .message(message)
                .data(null)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
