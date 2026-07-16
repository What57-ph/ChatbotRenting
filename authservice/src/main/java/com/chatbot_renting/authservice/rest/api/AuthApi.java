package com.chatbot_renting.authservice.rest.api;

import com.lecturemind.commonservice.domain.Request.LogoutRequest;
import com.lecturemind.commonservice.domain.Request.RefreshTokenRequest;
import com.lecturemind.commonservice.domain.Request.ReqLoginDTO;
import com.lecturemind.commonservice.domain.Request.SignupRequest;
import com.lecturemind.commonservice.domain.Response.ApiResponse;
import com.lecturemind.commonservice.domain.Response.ResLoginDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("/client-api/v1/auth")
public interface AuthApi {

    @PostMapping("/signup")
    ResponseEntity<ApiResponse<String>> signup(@RequestBody SignupRequest signupRequest);

    @PostMapping("/validate-token")
    ResponseEntity<UUID> validateToken(@RequestParam String token);

    @PostMapping("/login")
    ResponseEntity<ApiResponse<ResLoginDTO>> login(@RequestBody ReqLoginDTO loginRequest) throws Exception;

    @PostMapping("/refresh-token")
    ResponseEntity<ApiResponse<ResLoginDTO>> refreshToken(@RequestBody RefreshTokenRequest request) throws Exception;

    @PostMapping("/logout")
    ResponseEntity<ApiResponse<String>> logout(@RequestBody LogoutRequest request);
}
