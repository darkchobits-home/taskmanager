package com.sebastien.taskmanager.entity.role;

import com.sebastien.taskmanager.enums.RoleEnum;
import lombok.Data;

@Data
public class Role {
    private Long id;

    private RoleEnum name;

    private String description;
}
