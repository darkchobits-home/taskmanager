package com.sebastien.taskmanager.model;

import com.sebastien.taskmanager.enums.RoleEnum;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity (name = "Role")
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "uc_role_name", columnNames = {"name"})
})
public class RoleModel implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true, nullable = false)
    @Enumerated(EnumType.STRING)
    private RoleEnum name;

    private String description;
}
