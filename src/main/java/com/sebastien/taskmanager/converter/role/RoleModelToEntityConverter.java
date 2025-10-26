package com.sebastien.taskmanager.converter.role;

import com.sebastien.taskmanager.converter.GenericConverter;
import com.sebastien.taskmanager.entity.role.Role;
import com.sebastien.taskmanager.model.RoleModel;
import org.springframework.stereotype.Component;

@Component
public class RoleModelToEntityConverter extends GenericConverter<RoleModel, Role> {
}
