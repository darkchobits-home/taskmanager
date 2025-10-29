package com.sebastien.taskmanager.dto.task;

import com.sebastien.taskmanager.enums.Status;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TaskUpdateDTO {

    private String title;

    private String description;

    private LocalDate dueDate;

    private Status status;
}
