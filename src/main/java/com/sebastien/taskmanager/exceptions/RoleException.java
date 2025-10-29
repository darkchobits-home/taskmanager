package com.sebastien.taskmanager.exceptions;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;


@Data
@EqualsAndHashCode(callSuper = true)
public class RoleException extends  RuntimeException {
    private static Logger logger = LoggerFactory.getLogger(RoleException.class);

    private RoleExceptionCode roleExceptionCode;

    private Map<String, String> details = new HashMap<>();

    public RoleException(final RoleExceptionCode roleExceptionCode) {
        super(roleExceptionCode.getMessage());
        this.roleExceptionCode = roleExceptionCode;

        logger.warn("Role Exception : code = {}", this.roleExceptionCode);
    }
}
