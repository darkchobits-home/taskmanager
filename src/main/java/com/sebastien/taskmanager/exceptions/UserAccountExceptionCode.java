package com.sebastien.taskmanager.exceptions;

import lombok.Getter;


@Getter
public enum UserAccountExceptionCode {

    UNKNOWN_EXCEPTION(-1, "UserAccount unknown exception."),
    USERNAME_ALREADY_EXISTS(1, "Username already exists."),
    NO_ROLE_DEFINED(2, "No role defined."),
    USER_ACCOUNT_NOT_FOUND(3, "UserAccount not found.")
    ;

    private final int codeValue;

    private final String message;

    UserAccountExceptionCode(int codeValue, String message) {
        this.codeValue = codeValue;
        this.message = message;
    }
}
