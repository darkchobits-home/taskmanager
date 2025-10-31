package com.sebastien.taskmanager.exceptions;

import lombok.Getter;


@Getter
public enum TaskExceptionCode {

    UNKNOWN_EXCEPTION(-1, "Unknown exception."),
    TASK_ID_DOES_NOT_EXIST(1, "RoleId doesn't exist.")
    ;

    private final int codeValue;

    private final String message;

    TaskExceptionCode(int codeValue, String message) {
        this.codeValue = codeValue;
        this.message = message;
    }
}
