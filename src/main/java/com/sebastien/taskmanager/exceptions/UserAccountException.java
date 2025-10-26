package com.sebastien.taskmanager.exceptions;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;


@Data
@EqualsAndHashCode(callSuper = true)
public class UserAccountException extends  RuntimeException {
    private static Logger logger = LoggerFactory.getLogger(UserAccountException.class);

    private UserAccountExceptionCode userAccountExceptionCode;

    private Map<String, String> details = new HashMap<>();

    public UserAccountException(final UserAccountExceptionCode userAccountExceptionCode) {
        super(userAccountExceptionCode.getMessage());
        this.userAccountExceptionCode = userAccountExceptionCode;
        logger.warn("UserAccount Exception : code = {}", this.userAccountExceptionCode);
    }
}
