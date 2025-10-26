package com.sebastien.taskmanager.exceptions;

import lombok.Getter;

import java.util.Arrays;


@Getter
public enum UserAccountExceptionCode {

    UNKNOWN_EXCEPTION(-1, "Error code <%s> not defined"),
    USERNAME_ALREADY_EXISTS(1, "Username already exists"),
    NO_ROLE_DEFINED(2, "No role defined")
    ;

    private int codeValue;

    private String message;

    UserAccountExceptionCode(int codeValue, String message) {
        this.codeValue = codeValue;
        this.message = message;
    }

    public static boolean isCodeExisting(int code) {
        return Arrays.stream(UserAccountExceptionCode.values()).anyMatch(userAccountExceptionCode -> userAccountExceptionCode.getCodeValue() == code);
    }

    public static String getMessageFromCode(int code) {
        return Arrays.stream(UserAccountExceptionCode.values()).filter(userAccountExceptionCode -> userAccountExceptionCode.getCodeValue() == code).findFirst().map(UserAccountExceptionCode::getMessage).orElse("");
    }
}
