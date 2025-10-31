package com.sebastien.taskmanager.exceptions;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;


@Data
@EqualsAndHashCode(callSuper = true)
public class TaskException extends  RuntimeException {
    private static Logger logger = LoggerFactory.getLogger(TaskException.class);

    private TaskExceptionCode taskExceptionCode;

    private Map<String, String> details = new HashMap<>();

    public TaskException(final TaskExceptionCode taskExceptionCode) {
        super(taskExceptionCode.getMessage());
        this.taskExceptionCode = taskExceptionCode;

        logger.warn("Task Exception : code = {}", this.taskExceptionCode);
    }
}
