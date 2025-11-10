package com.sebastien.taskmanager.controller.error;

import com.sebastien.taskmanager.exceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(UserAccountException.class)
    @SuppressWarnings("unused")
    public ResponseEntity<Object> handleUserAccountException(UserAccountException userAccountException, WebRequest webRequest) {
        // Status
        final HttpStatus status = getHttpStatusFromUserAccountExceptionCode(userAccountException.getUserAccountExceptionCode());

        // Path
        final String path = ((ServletWebRequest) webRequest).getRequest().getRequestURI();

        // Creates a standard error response using exception data.
        final ErrorResponse errorResponse = new ErrorResponse(
                status,
                userAccountException.getUserAccountExceptionCode().getCodeValue(),
                userAccountException.getMessage(),
                path);
        errorResponse.setDetails(userAccountException.getDetails());

        logger.error(userAccountException.getMessage());

        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(RoleException.class)
    @SuppressWarnings("unused")
    public ResponseEntity<Object> handleRoleException(RoleException roleException, WebRequest webRequest) {
        // Status
        final HttpStatus status = getHttpStatusFromRoleExceptionCode(roleException.getRoleExceptionCode());

        // Path
        final String path = ((ServletWebRequest) webRequest).getRequest().getRequestURI();

        // Creates a standard error response using exception data.
        final ErrorResponse errorResponse = new ErrorResponse(
                status,
                roleException.getRoleExceptionCode().getCodeValue(),
                roleException.getMessage(),
                path);
        errorResponse.setDetails(roleException.getDetails());

        logger.error(roleException.getMessage());

        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(TaskException.class)
    @SuppressWarnings("unused")
    public ResponseEntity<Object> handleRoleException(TaskException taskException, WebRequest webRequest) {
        // Status
        final HttpStatus status = getHttpStatusFromTaskExceptionCode(taskException.getTaskExceptionCode());

        // Path
        final String path = ((ServletWebRequest) webRequest).getRequest().getRequestURI();

        // Creates a standard error response using exception data.
        final ErrorResponse errorResponse = new ErrorResponse(
                status,
                taskException.getTaskExceptionCode().getCodeValue(),
                taskException.getMessage(),
                path);
        errorResponse.setDetails(taskException.getDetails());

        logger.error(taskException.getMessage());

        return new ResponseEntity<>(errorResponse, status);
    }



    private HttpStatus getHttpStatusFromUserAccountExceptionCode(final UserAccountExceptionCode userAccountExceptionCode) {
        switch (userAccountExceptionCode) {
            case USERNAME_ALREADY_EXISTS ->  {
                return HttpStatus.CONFLICT;
            }
            case NO_ROLE_DEFINED -> {
                return HttpStatus.BAD_REQUEST;
            }
            default -> {
                return HttpStatus.FORBIDDEN;
            }
        }

    }

    private HttpStatus getHttpStatusFromRoleExceptionCode(final RoleExceptionCode roleExceptionCode) {
        switch (roleExceptionCode) {
            case ROLE_NAME_ALREADY_EXISTS ->  {
                return HttpStatus.CONFLICT;
            }
            case ROLE_ID_DOES_NOT_EXIST -> {
                return HttpStatus.BAD_REQUEST;
            }
            default -> {
                return HttpStatus.FORBIDDEN;
            }
        }

    }

    private HttpStatus getHttpStatusFromTaskExceptionCode(TaskExceptionCode taskExceptionCode) {
        switch (taskExceptionCode) {
            case TASK_ID_DOES_NOT_EXIST ->  {
                return HttpStatus.BAD_REQUEST;
            }
            default -> {
                return HttpStatus.FORBIDDEN;
            }
        }
    }
}
