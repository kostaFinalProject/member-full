package com.example.soccer.exception;

public class AuthException extends CustomException {

    public AuthException(ErrorCode errorCode) {
        super(errorCode);
    }
}
