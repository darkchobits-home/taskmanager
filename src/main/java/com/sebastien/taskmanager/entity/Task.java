package com.sebastien.taskmanager.entity;

import com.sebastien.taskmanager.enums.Status;
import jakarta.persistence.*;


import java.time.LocalDate;

@Entity
public class Task {

    @Id
    @GeneratedValue
    private Long id;

    private String title;

    private String description;

    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    private Status status;
}
