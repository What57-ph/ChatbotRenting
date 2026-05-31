package com.lecture_mind.authservice.service;

import com.lecture_mind.authservice.config.JwtUtil;
import com.lecture_mind.authservice.model.Role;
import com.lecture_mind.authservice.model.RoleType;
import com.lecture_mind.authservice.model.TokenType;
import com.lecture_mind.authservice.model.User;
import com.lecture_mind.authservice.repository.RoleRepository;
import com.lecture_mind.authservice.repository.UserRepository;
import com.lecturemind.commonservice.domain.Request.LogoutRequest;
import com.lecturemind.commonservice.domain.Request.RefreshTokenRequest;
import com.lecturemind.commonservice.domain.Request.ReqLoginDTO;
import com.lecturemind.commonservice.domain.Request.SignupRequest;
import com.lecturemind.commonservice.domain.Response.ResLoginDTO;
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
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtUtil jwtUtil;

    @Transactional
    public void signup(SignupRequest request) {

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
    }

    @Transactional
    public ResLoginDTO login(ReqLoginDTO reqLoginDTO) throws Exception {

        User user = userRepository.findByEmail(reqLoginDTO.getEmail())
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (!passwordEncoder.matches(reqLoginDTO.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Wrong password");
        }

        String accessToken = jwtService.createJwtToken(user, TokenType.ACCESS);
        String refreshToken = jwtService.createJwtToken(user, TokenType.REFRESH);

        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        return buildLoginResponse(user, accessToken, refreshToken);
    }

    public ResLoginDTO refreshToken(RefreshTokenRequest request) throws Exception {

        if (!jwtUtil.validateToken(request.getRefreshToken())) {
            throw new UnauthorizedException("Invalid refresh token");
        }

        String email = jwtUtil.extractUsername(request.getRefreshToken());

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Invalid email"));

        String newAccessToken = jwtService.createJwtToken(user, TokenType.ACCESS);

        return buildLoginResponse(user, newAccessToken, request.getRefreshToken());
    }

    public void logout(LogoutRequest logoutRequest) {

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
