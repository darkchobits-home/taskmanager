package com.sebastien.taskmanager.model;

import com.sebastien.taskmanager.enums.Priority;
import com.sebastien.taskmanager.enums.Status;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity (name = "Task")
public class TaskModel implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    private String title;

    private String description;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdAt;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    private Status status;

    private Priority priority;

    private UserAccountModel assignedTo;
}
