package com.lecture_mind.authservice.config;

import com.fasterxml.jackson.databind.ObjectMapper; // Sửa lại package chuẩn của Jackson
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lecturemind.commonservice.domain.Response.ResponseBuilder; // Giả định class này trả về Object hoặc String
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException)
            throws IOException, ServletException {

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        Object errorResponse = ResponseBuilder.error(
                HttpStatus.UNAUTHORIZED,
                "Full authentication is required to access this resource"
        );

        response.getWriter().write(OBJECT_MAPPER.writeValueAsString(errorResponse));
    }
}