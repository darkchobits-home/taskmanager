package com.sebastien.taskmanager.dto.useraccount;

import com.sebastien.taskmanager.dto.role.RoleUpdateDTO;
import lombok.Data;

import java.util.List;

@Data
public class UserAccountUpdateDTO {
    private Long id;

    private String username;

    private String password;

    private List<RoleUpdateDTO> roles;
}
