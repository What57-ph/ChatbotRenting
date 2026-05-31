package com.lecture_mind.authservice.controller;

import com.lecture_mind.authservice.config.JwtUtil;
import com.lecture_mind.authservice.service.AuthService;
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
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<String>> signup(@RequestBody SignupRequest signupRequest) {
        authService.signup(signupRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResponseBuilder.created("Account is registered successfully."));
    }

    @PostMapping("/validate-token")
    public ResponseEntity<Long> validateToken(@RequestParam String token) {
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Long userId = jwtUtil.extractUserId(token);
        log.debug("User Id: "+userId);
        return ResponseEntity.ok(userId);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<ResLoginDTO>> login(@RequestBody ReqLoginDTO loginRequest) throws Exception {

        ResLoginDTO resLoginDTO = authService.login(loginRequest);

        return ResponseEntity.ok(
                ResponseBuilder.success(resLoginDTO)
        );
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<ResLoginDTO>> refreshToken(
            @RequestBody RefreshTokenRequest request) throws Exception {

        ResLoginDTO res = authService.refreshToken(request);

        return ResponseEntity.ok(
                ResponseBuilder.success(res)
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(
            @RequestBody LogoutRequest request) {

        authService.logout(request);

        return ResponseEntity.ok(
                ResponseBuilder.success("Logout successfully")
        );
    }
}