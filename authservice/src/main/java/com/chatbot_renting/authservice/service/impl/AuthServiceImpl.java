package com.chatbot_renting.authservice.service.impl;

import com.chatbot_renting.authservice.config.JwtUtil;
import com.chatbot_renting.authservice.entity.Role;
import com.chatbot_renting.authservice.entity.RoleType;
import com.chatbot_renting.authservice.entity.TokenType;
import com.chatbot_renting.authservice.entity.User;
import com.chatbot_renting.authservice.repository.RoleRepository;
import com.chatbot_renting.authservice.repository.UserRepository;
import com.chatbot_renting.authservice.service.AuthService;
import com.chatbot_renting.commonservice.domain.Request.LogoutRequest;
import com.chatbot_renting.commonservice.domain.Request.RefreshTokenRequest;
import com.chatbot_renting.commonservice.domain.Request.ReqLoginDTO;
import com.chatbot_renting.commonservice.domain.Request.SignupRequest;
import com.chatbot_renting.commonservice.domain.Response.ResLoginDTO;
import com.lecturemind.commonservice.exception.AuthException;
import com.lecturemind.commonservice.exception.ExistException;
import com.lecturemind.commonservice.exception.NotFoundException;
import com.lecturemind.commonservice.exception.UnauthorizedException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public void signup(SignupRequest request) {
        log.info("Processing signup for email: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ExistException("Email already exists");
        }

        Role role = roleRepository.findByName(RoleType.USER.name())
                .orElseThrow(() -> new NotFoundException("Role USER not found"));

        User user = User.builder()
                .email(request.getEmail())
                .avatarUrl(request.getAvatarUrl())
                .fullName(request.getFullName())
                .isActive(true)
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(List.of(role))
                .build();

        userRepository.save(user);
        log.info("Signup completed for email: {}", request.getEmail());
    }

    @Override
    @Transactional
    public ResLoginDTO login(ReqLoginDTO reqLoginDTO) throws Exception {
        log.info("Processing login for email: {}", reqLoginDTO.getEmail());

        User user = userRepository.findByEmail(reqLoginDTO.getEmail())
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (!passwordEncoder.matches(reqLoginDTO.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Wrong password");
        }

        String accessToken = jwtService.createJwtToken(user, TokenType.ACCESS);
        String refreshToken = jwtService.createJwtToken(user, TokenType.REFRESH);

        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        log.info("Login successful for email: {}", reqLoginDTO.getEmail());
        return buildLoginResponse(user, accessToken, refreshToken);
    }

    @Override
    public ResLoginDTO refreshToken(RefreshTokenRequest request) throws Exception {
        log.info("Processing refresh token");

        if (!jwtUtil.validateToken(request.getRefreshToken())) {
            throw new UnauthorizedException("Invalid refresh token");
        }

        String email = jwtUtil.extractUsername(request.getRefreshToken());

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Invalid email"));

        String newAccessToken = jwtService.createJwtToken(user, TokenType.ACCESS);

        log.info("Refresh token successful for email: {}", email);
        return buildLoginResponse(user, newAccessToken, request.getRefreshToken());
    }

    @Override
    public void logout(LogoutRequest logoutRequest) {
        log.info("Processing logout");

        if (logoutRequest == null || logoutRequest.getRefreshToken() == null) {
            throw new AuthException("Refresh token is required");
        }

        if (!jwtUtil.validateToken(logoutRequest.getRefreshToken())) {
            throw new UnauthorizedException("Invalid refresh token");
        }

        User user = userRepository.findUserByRefreshToken(logoutRequest.getRefreshToken())
                .orElseThrow(() -> new UnauthorizedException("Invalid token for user"));

        user.setRefreshToken(null);
        userRepository.save(user);
        log.info("Logout successful");
    }

    private ResLoginDTO buildLoginResponse(User user, String accessToken, String refreshToken) {

        ResLoginDTO.UserLogin userLogin = ResLoginDTO.UserLogin.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .roles(
                        user.getRoles() == null
                                ? List.of()
                                : user.getRoles().stream()
                                .map(Role::getName)
                                .toList()
                )
                .build();

        return ResLoginDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userLogin(userLogin)
                .build();
    }
}
