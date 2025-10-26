package com.sebastien.taskmanager.converter.useraccount;

import com.sebastien.taskmanager.converter.GenericConverter;
import com.sebastien.taskmanager.converter.role.RoleEntityToDtoConverter;
import com.sebastien.taskmanager.dto.useraccount.UserAccountDTO;
import com.sebastien.taskmanager.entity.useraccount.UserAccount;
import org.modelmapper.TypeMap;
import org.springframework.stereotype.Component;

@Component
public class UserAccountEntityToDtoConverter extends GenericConverter<UserAccount, UserAccountDTO> {

    public UserAccountEntityToDtoConverter(RoleEntityToDtoConverter roleEntityToDtoConverter) {
        TypeMap<UserAccount, UserAccountDTO> typeMap = modelMapper.createTypeMap(UserAccount.class, UserAccountDTO.class)
                .addMappings(mapper -> mapper.using(roleEntityToDtoConverter).map(UserAccount::getRoles, UserAccountDTO::setRoles));
    }
}
