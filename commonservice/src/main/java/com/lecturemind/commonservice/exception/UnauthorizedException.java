package com.lecturemind.commonservice.exception;

public class UnauthorizedException extends AuthException{
    public UnauthorizedException(String message) {
        super(message);
    }
}
