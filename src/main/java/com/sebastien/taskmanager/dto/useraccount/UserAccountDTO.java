package com.sebastien.taskmanager.dto.useraccount;

import com.sebastien.taskmanager.dto.role.RoleDTO;
import lombok.Data;

import java.util.List;

@Data
public class UserAccountDTO {
    private String username;

    private String email;

    private String password;

    private List<RoleDTO> roles;
}
