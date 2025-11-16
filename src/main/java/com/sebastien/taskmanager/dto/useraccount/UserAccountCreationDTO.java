package com.sebastien.taskmanager.dto.useraccount;

import com.sebastien.taskmanager.dto.role.RoleCreationDTO;
import lombok.Data;

import java.util.List;

@Data
public class UserAccountCreationDTO {

    private String username;

    private String password;

    private List<RoleCreationDTO> roles;
}
