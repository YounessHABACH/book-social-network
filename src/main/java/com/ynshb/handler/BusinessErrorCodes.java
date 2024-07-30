package com.ynshb.handler;

import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public enum BusinessErrorCodes {

    NO_CODE(0, "No code", HttpStatus.NOT_IMPLEMENTED),
    INCORRECT_CURRENT_PASSWORD(300, "Incorrect current password", HttpStatus.BAD_REQUEST),
    NO_PASSWORD_MATCH(301, "No password match", HttpStatus.BAD_REQUEST),
    ACCOUNT_LOCKED(302, "Account locked", HttpStatus.LOCKED),
    ACCOUNT_DISABLED(303, "Account disabled", HttpStatus.FORBIDDEN),
    BAD_CREDENTIALS(304, "Bad credentials", HttpStatus.UNAUTHORIZED),
    ;

    private final int code;
    private final String description;
    private final HttpStatus httpStatus;

    BusinessErrorCodes(int code, String description, HttpStatus httpStatus) {
        this.code = code;
        this.description = description;
        this.httpStatus = httpStatus;
    }
}
