package com.sebastien.taskmanager.entity.task;

import com.sebastien.taskmanager.enums.Status;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class Task {

    private Long id;

    private String title;

    private String description;

    @DateTimeFormat(pattern = "YYYY-MM-DD")
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    private Status status;
}
