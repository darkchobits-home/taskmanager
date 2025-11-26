package com.sebastien.taskmanager.dto.role;

import com.sebastien.taskmanager.enums.RoleEnum;
import lombok.Data;

@Data
public class RoleDTO {
    private RoleEnum name;

    private String description;
}
