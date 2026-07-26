package com.chatbot_renting.authservice.rest.controller;

import com.chatbot_renting.authservice.config.JwtUtil;
import com.chatbot_renting.authservice.rest.api.AuthApi;
import com.chatbot_renting.authservice.service.AuthService;
import com.lecturemind.commonservice.domain.Request.LogoutRequest;
import com.lecturemind.commonservice.domain.Request.RefreshTokenRequest;
import com.lecturemind.commonservice.domain.Request.ReqLoginDTO;
import com.lecturemind.commonservice.domain.Request.SignupRequest;
import com.lecturemind.commonservice.domain.Response.ApiResponse;
import com.lecturemind.commonservice.domain.Response.ResLoginDTO;
import com.lecturemind.commonservice.domain.Response.ResponseBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController implements AuthApi {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    @Override
    public ResponseEntity<ApiResponse<String>> signup(SignupRequest signupRequest) {
        log.info("REST request to signup with email: {}", signupRequest.getEmail());
        authService.signup(signupRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResponseBuilder.created("Account is registered successfully."));
    }

    @Override
    public ResponseEntity<UUID> validateToken(String token) {
        log.info("REST request to validate token");
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UUID userId = jwtUtil.extractUserId(token);
        log.debug("User Id: {}", userId);
        return ResponseEntity.ok(userId);
    }

    @Override
    public ResponseEntity<ApiResponse<ResLoginDTO>> login(ReqLoginDTO loginRequest) throws Exception {
        log.info("REST request to login with email: {}", loginRequest.getEmail());
        ResLoginDTO resLoginDTO = authService.login(loginRequest);
        return ResponseEntity.ok(ResponseBuilder.success(resLoginDTO));
    }

    @Override
    public ResponseEntity<ApiResponse<ResLoginDTO>> refreshToken(RefreshTokenRequest request) throws Exception {
        log.info("REST request to refresh token");
        ResLoginDTO res = authService.refreshToken(request);
        return ResponseEntity.ok(ResponseBuilder.success(res));
    }

    @Override
    public ResponseEntity<ApiResponse<String>> logout(LogoutRequest request) {
        log.info("REST request to logout");
        authService.logout(request);
        return ResponseEntity.ok(ResponseBuilder.success("Logout successfully"));
    }
}
