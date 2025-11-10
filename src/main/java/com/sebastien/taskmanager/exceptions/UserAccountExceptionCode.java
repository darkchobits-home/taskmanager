package com.sebastien.taskmanager.exceptions;

import lombok.Getter;


@Getter
public enum UserAccountExceptionCode {

    UNKNOWN_EXCEPTION(-1, "UserAccount unknown exception."),
    USERNAME_ALREADY_EXISTS(1, "Username already exists."),
    NO_ROLE_DEFINED(2, "No role defined."),
    USER_ACCOUNT_ID_DOES_NOT_EXIST(3, "UserAccountId doesn't exist.")
    ;

    private final int codeValue;

    private final String message;

    UserAccountExceptionCode(int codeValue, String message) {
        this.codeValue = codeValue;
        this.message = message;
    }
}
