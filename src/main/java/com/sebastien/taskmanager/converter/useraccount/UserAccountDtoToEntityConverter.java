package com.sebastien.taskmanager.converter.useraccount;

import com.sebastien.taskmanager.converter.GenericConverter;
import com.sebastien.taskmanager.converter.role.RoleDtoToEntityConverter;
import com.sebastien.taskmanager.dto.useraccount.UserAccountCreationDTO;
import com.sebastien.taskmanager.dto.useraccount.UserAccountDTO;
import com.sebastien.taskmanager.dto.useraccount.UserAccountUpdateDTO;
import com.sebastien.taskmanager.entity.useraccount.UserAccount;
import org.springframework.stereotype.Component;

@Component
public class UserAccountDtoToEntityConverter extends GenericConverter<UserAccountDTO, UserAccount> {

    public UserAccountDtoToEntityConverter(RoleDtoToEntityConverter roleDtoToEntityConverter) {
        modelMapper.addConverter(roleDtoToEntityConverter);
    }

    public UserAccount convertCreationDtoToEntity(final UserAccountCreationDTO userAccountCreationDTO) {
        return modelMapper.map(userAccountCreationDTO, UserAccount.class);
    }

    public UserAccount convertUpdateDtoToEntity(UserAccountUpdateDTO userAccountUpdateDTO) {
        return modelMapper.map(userAccountUpdateDTO, UserAccount.class);
    }
}
