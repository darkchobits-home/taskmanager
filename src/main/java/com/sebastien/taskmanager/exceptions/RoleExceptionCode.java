package com.sebastien.taskmanager.exceptions;

import lombok.Getter;


@Getter
public enum RoleExceptionCode {

    UNKNOWN_EXCEPTION(-1, "Unknown exception."),
    ROLE_NAME_ALREADY_EXISTS(1, "Role name already exists."),
    ROLE_ID_DOES_NOT_EXIST(2, "RoleId doesn't exist.")
    ;

    private int codeValue;

    private String message;

    RoleExceptionCode(int codeValue, String message) {
        this.codeValue = codeValue;
        this.message = message;
    }
}
