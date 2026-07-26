package com.chatbot_renting.authservice.service.impl;

import com.chatbot_renting.authservice.config.JwtUtil;
import com.chatbot_renting.authservice.entity.TokenType;
import com.chatbot_renting.authservice.entity.User;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtService {
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
