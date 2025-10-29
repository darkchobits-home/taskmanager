package com.sebastien.taskmanager.dto.useraccount;

import com.sebastien.taskmanager.dto.role.RoleUpdateDTO;
import lombok.Data;

import java.util.List;

@Data
public class UserAccountUpdateDTO {
    private String username;

    private String email;

    private String password;

    private List<RoleUpdateDTO> roles;
}
