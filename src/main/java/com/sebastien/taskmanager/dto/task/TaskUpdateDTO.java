package com.sebastien.taskmanager.dto.task;

import com.sebastien.taskmanager.dto.useraccount.UserAccountCreationDTO;
import com.sebastien.taskmanager.enums.Priority;
import com.sebastien.taskmanager.enums.Status;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class TaskUpdateDTO {

    private Long id;

    private String title;

    private String description;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdAt;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime updatedAt;

    private Status status;

    private Priority priority;

    private UserAccountCreationDTO assignedTo;
}
