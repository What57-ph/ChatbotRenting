package com.chatbotrenting.apigateway.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.util.StringUtils;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Token {

    String accessToken;
    Long accessTokenExpiresAt;
    String refreshToken;

    private static final String TOKEN_PREFIX = "Bearer ";

    public static boolean isBearerToken(final String authorizationHeader){
        return StringUtils.hasText(authorizationHeader) &&
                authorizationHeader.startsWith(TOKEN_PREFIX);
    }

    public static String getJwt(final String authorizationHeader){
        return authorizationHeader.replace(TOKEN_PREFIX,"");
    }

}
