package com.server.exception;

import lombok.Getter;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public enum ExceptionCode {

    /* MEMBER */
    MEMBER_NOT_FOUND(404, "Member Not Found"),
    MEMBER_EXISTS(409, "Member Exists"),
    MEMBER_NOT_AUTHORIZED(405, "Member Not Authorized"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),

    /* BLOG */
    BLOG_NOT_FOUND(404, "Blog Not Found"),

    /* CATEGORY */
    CATEGORY_NOT_FOUND(404, "Category Not Found"),
    CATEGORY_ID_NOT_ALLOWED(405, "CategoryId Not Allowed"),
    CATEGORY_EXISTS(409, "Category Exists"),

    /* COMMENT */
    COMMENT_NOT_FOUND(404, "Comment Not Found"),
    COMMENT_STATUS_INVALID(404, "Comment status is inValid"),

    /* TOKEN */
    ACCESS_TOKEN_EXPIRATION(401, "Access Token Expiration"),
    REFRESH_TOKEN_EXPIRATION(401, "Refresh Token Expiration"),

    /* IMAGE */
    IMAGE_FILE_NOT_FONUD(404, "Image file not found"),
    INVALID_TOKEN(400, "Invalid token");

    @Getter
    private int status;

    @Getter
    private String message;

    ExceptionCode(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
