package com.chatbot_renting.authservice.service;

import com.chatbot_renting.commonservice.domain.Request.LogoutRequest;
import com.chatbot_renting.commonservice.domain.Request.RefreshTokenRequest;
import com.chatbot_renting.commonservice.domain.Request.ReqLoginDTO;
import com.chatbot_renting.commonservice.domain.Request.SignupRequest;
import com.chatbot_renting.commonservice.domain.Response.ResLoginDTO;

public interface AuthService {

    void signup(SignupRequest request);

    ResLoginDTO login(ReqLoginDTO reqLoginDTO) throws Exception;

    ResLoginDTO refreshToken(RefreshTokenRequest request) throws Exception;

    void logout(LogoutRequest logoutRequest);
}
