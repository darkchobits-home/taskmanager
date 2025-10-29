package com.sebastien.taskmanager.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity (name = "Role")
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "uc_role_name", columnNames = {"name"})
})
public class RoleModel {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String name;
}
