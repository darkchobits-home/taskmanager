package com.sebastien.taskmanager.converter.useraccount;

import com.sebastien.taskmanager.converter.GenericConverter;
import com.sebastien.taskmanager.converter.role.RoleDtoToEntityConverter;
import com.sebastien.taskmanager.dto.useraccount.UserAccountCreationDTO;
import com.sebastien.taskmanager.dto.useraccount.UserAccountDTO;
import com.sebastien.taskmanager.entity.useraccount.UserAccount;
import org.modelmapper.TypeMap;
import org.springframework.stereotype.Component;

@Component
public class UserAccountDtoToEntityConverter extends GenericConverter<UserAccountDTO, UserAccount> {

    public UserAccountDtoToEntityConverter(RoleDtoToEntityConverter roleDtoToEntityConverter) {
        TypeMap<UserAccountDTO, UserAccount> typeMap = modelMapper.createTypeMap(UserAccountDTO.class, UserAccount.class)
                .addMappings(mapper -> mapper.using(roleDtoToEntityConverter).map(UserAccountDTO::getRoles, UserAccount::setRoles));
    }

    public UserAccount convertCreationDtoToEntity(final UserAccountCreationDTO userAccountCreationDTO) {
        return modelMapper.map(userAccountCreationDTO, UserAccount.class);
    }
}
