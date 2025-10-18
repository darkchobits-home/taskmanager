package com.sebastien.taskmanager.dto.useraccount;

import com.sebastien.taskmanager.model.RoleModel;

import java.util.List;

public class UserCreationDTO {
    private String name;

    private String password;

    private List<RoleModel> roleModels;
}
