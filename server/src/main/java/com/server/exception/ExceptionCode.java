package com.server.exception;

import lombok.Getter;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public enum ExceptionCode {
    MEMBER_NOT_FOUND(404, "Member Not Found"),
    MEMBER_EXISTS(409, "Member Exists"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),

    // Blog
    BLOG_NOT_FOUND(404, "Blog Not Found"),


    // Category
    CATEGORY_NOT_FOUND(404, "Category Not Found"),
    CATEGORY_ID_NOT_ALLOWED(405, "CategoryId Not Allowed"),
    CATEGORY_EXISTS(409, "Category Exists");

    @Getter
    private int status;

    @Getter
    private String message;

    ExceptionCode(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
