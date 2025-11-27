package com.sebastien.taskmanager.dto.useraccount;

import com.sebastien.taskmanager.dto.role.RoleUpdateDTO;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class UserAccountUpdateDTO {
    private Long id;

    @Email
    @NotEmpty
    private String username;

    @NotEmpty
    private String password;

    @NotEmpty
    private List<RoleUpdateDTO> roles;
}
