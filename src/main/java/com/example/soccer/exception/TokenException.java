package com.example.soccer.exception;



public class TokenException extends CustomException {

    public TokenException(ErrorCode errorCode) {
        super(errorCode);
    }
}