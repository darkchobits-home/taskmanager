package com.sebastien.taskmanager.dto.role;

import com.sebastien.taskmanager.enums.RoleEnum;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class RoleCreationDTO {

    @NotEmpty
    private RoleEnum name;

    private String description;
}
