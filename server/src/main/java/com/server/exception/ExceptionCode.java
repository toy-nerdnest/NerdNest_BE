package com.server.exception;

import lombok.Getter;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public enum ExceptionCode {
    MEMBER_NOT_FOUND(404, "Member Not Found"),
    MEMBER_EXISTS(409, "Member Exists"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    ACCESS_TOKEN_EXPIRATION(401, "Access Token Expiration"),
    REFRESH_TOKEN_EXPIRATION(401, "Refresh Token Expiration"),
    INVALID_TOKEN(400, "Invalid token"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error");

    @Getter
    private int status;

    @Getter
    private String message;

    ExceptionCode(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
