package com.sebastien.taskmanager.converter.useraccount;

import com.sebastien.taskmanager.converter.GenericConverter;
import com.sebastien.taskmanager.converter.role.RoleEntityToModelConverter;
import com.sebastien.taskmanager.entity.role.Role;
import com.sebastien.taskmanager.entity.useraccount.UserAccount;
import com.sebastien.taskmanager.model.RoleModel;
import com.sebastien.taskmanager.model.UserAccountModel;
import org.modelmapper.TypeMap;
import org.springframework.stereotype.Component;

@Component
public class UserAccountEntityToModelConverter extends GenericConverter<UserAccount, UserAccountModel> {

    public UserAccountEntityToModelConverter(RoleEntityToModelConverter roleEntityToModelConverter) {
        modelMapper.addConverter(roleEntityToModelConverter);
    }
}
