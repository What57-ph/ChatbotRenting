package com.chatbot_renting.authservice.service;

import com.lecturemind.commonservice.domain.Request.LogoutRequest;
import com.lecturemind.commonservice.domain.Request.RefreshTokenRequest;
import com.lecturemind.commonservice.domain.Request.ReqLoginDTO;
import com.lecturemind.commonservice.domain.Request.SignupRequest;
import com.lecturemind.commonservice.domain.Response.ResLoginDTO;

public interface AuthService {

    void signup(SignupRequest request);

    ResLoginDTO login(ReqLoginDTO reqLoginDTO) throws Exception;

    ResLoginDTO refreshToken(RefreshTokenRequest request) throws Exception;

    void logout(LogoutRequest logoutRequest);
}
