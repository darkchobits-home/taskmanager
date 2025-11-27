package com.sebastien.taskmanager.dto.useraccount;

import com.sebastien.taskmanager.dto.role.RoleCreationDTO;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class UserAccountCreationDTO {

    @Email
    @NotEmpty
    private String username;

    @NotEmpty
    @Size(min = 6)
    private String password;

    @NotEmpty
    private List<RoleCreationDTO> roles;
}
