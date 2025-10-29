package com.sebastien.taskmanager.controller.error;

import com.sebastien.taskmanager.exceptions.UserAccountException;
import com.sebastien.taskmanager.exceptions.UserAccountExceptionCode;
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

        return new ResponseEntity<>(errorResponse, status);
    }

    private HttpStatus getHttpStatusFromUserAccountExceptionCode(final UserAccountExceptionCode userAccountExceptionCode) {
        switch (userAccountExceptionCode) {
            case USERNAME_ALREADY_EXISTS, NO_ROLE_DEFINED -> {
                return HttpStatus.BAD_REQUEST;
            }
            default -> {
                return HttpStatus.FORBIDDEN;
            }
        }

    }
}
