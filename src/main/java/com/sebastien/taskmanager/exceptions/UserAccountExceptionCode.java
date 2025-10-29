package com.sebastien.taskmanager.exceptions;

import lombok.Getter;


@Getter
public enum UserAccountExceptionCode {

    UNKNOWN_EXCEPTION(-1, "Unknown exception."),
    USERNAME_ALREADY_EXISTS(1, "Username already exists."),
    NO_ROLE_DEFINED(2, "No role defined."),
    USER_ACCOUNT_ID_DOES_NOT_EXIST(3, "UserAccountId doesn't exist.")
    ;

    private int codeValue;

    private String message;

    UserAccountExceptionCode(int codeValue, String message) {
        this.codeValue = codeValue;
        this.message = message;
    }
}
