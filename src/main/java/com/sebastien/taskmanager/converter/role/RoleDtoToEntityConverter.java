package com.sebastien.taskmanager.converter.role;

import com.sebastien.taskmanager.converter.GenericConverter;
import com.sebastien.taskmanager.dto.role.RoleCreationDTO;
import com.sebastien.taskmanager.dto.role.RoleDTO;
import com.sebastien.taskmanager.dto.role.RoleUpdateDTO;
import com.sebastien.taskmanager.entity.role.Role;

public class RoleDtoToEntityConverter extends GenericConverter<RoleDTO, Role> {

    public Role convertCreationDtoToEntity(final RoleCreationDTO roleCreationDTO) {
        return modelMapper.map(roleCreationDTO, Role.class);
    }

    public Role convertUpdateDtoToEntity(RoleUpdateDTO roleUpdateDTO) {
        return modelMapper.map(roleUpdateDTO, Role.class);
    }
}
