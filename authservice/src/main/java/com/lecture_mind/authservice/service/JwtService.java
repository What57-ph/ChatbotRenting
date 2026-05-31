package com.lecture_mind.authservice.service;

import com.lecture_mind.authservice.config.JwtUtil;
import com.lecture_mind.authservice.model.TokenType;
import com.lecture_mind.authservice.model.User;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailService customUserDetailService;
    private final JwtUtil jwtUtil;

    public String createJwtToken(User user, TokenType tokenType) throws Exception {
        UserDetails userDetails = customUserDetailService.loadUserByUsername(user.getEmail());
        Date expiration = tokenType == TokenType.ACCESS
                ? DateUtils.addMinutes(new Date(), 60)
                : DateUtils.addDays(new Date(), 30);
        return jwtUtil.generateToken(userDetails, expiration);
    }


}
