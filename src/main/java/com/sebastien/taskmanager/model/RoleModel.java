package com.sebastien.taskmanager.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity (name = "Role")
public class RoleModel {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
}
