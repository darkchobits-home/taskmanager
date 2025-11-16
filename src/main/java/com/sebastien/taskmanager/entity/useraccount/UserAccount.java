package com.sebastien.taskmanager.entity.useraccount;

import com.sebastien.taskmanager.entity.role.Role;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class UserAccount {

    private Long id;

    private String username;

    private String password;

    private Set<Role> roles = new HashSet<>();
}
