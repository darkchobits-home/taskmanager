package com.sebastien.taskmanager.dto.useraccount;

import com.sebastien.taskmanager.dto.role.RoleDTO;
import lombok.Data;

import java.util.List;

@Data
public class UserAccountDTO {
    private Long id;

    private String username;

    private String password;

    private List<RoleDTO> roles;
}
