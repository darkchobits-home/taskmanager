package com.sebastien.taskmanager.converter.useraccount;

import com.sebastien.taskmanager.converter.GenericConverter;
import com.sebastien.taskmanager.converter.role.RoleModelToEntityConverter;
import com.sebastien.taskmanager.entity.useraccount.UserAccount;
import com.sebastien.taskmanager.model.UserAccountModel;
import org.modelmapper.TypeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserAccountModelToEntityConverter extends GenericConverter<UserAccountModel, UserAccount> {

    @Autowired
    private RoleModelToEntityConverter roleModelToEntityConverter;

    public UserAccountModelToEntityConverter(RoleModelToEntityConverter roleModelToEntityConverter) {
        TypeMap<UserAccountModel, UserAccount> typeMap = modelMapper.createTypeMap(UserAccountModel.class, UserAccount.class)
                .addMappings(mapper -> mapper.using(roleModelToEntityConverter).map(UserAccountModel::getRoles, UserAccount::setRoles));
    }

}
