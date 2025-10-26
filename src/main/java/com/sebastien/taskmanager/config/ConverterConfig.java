package com.sebastien.taskmanager.config;

import com.sebastien.taskmanager.converter.role.RoleDtoToEntityConverter;
import com.sebastien.taskmanager.converter.role.RoleEntityToDtoConverter;
import com.sebastien.taskmanager.converter.role.RoleEntityToModelConverter;
import com.sebastien.taskmanager.converter.role.RoleModelToEntityConverter;
import com.sebastien.taskmanager.converter.task.TaskDtoToEntityConverter;
import com.sebastien.taskmanager.converter.task.TaskEntityToDtoConverter;
import com.sebastien.taskmanager.converter.task.TaskEntityToModelConverter;
import com.sebastien.taskmanager.converter.task.TaskModelToEntityConverter;
import com.sebastien.taskmanager.converter.useraccount.UserAccountDtoToEntityConverter;
import com.sebastien.taskmanager.converter.useraccount.UserAccountEntityToDtoConverter;
import com.sebastien.taskmanager.converter.useraccount.UserAccountEntityToModelConverter;
import com.sebastien.taskmanager.converter.useraccount.UserAccountModelToEntityConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class ConverterConfig {

    // Tasks
    @Bean
    public TaskModelToEntityConverter taskModelToEntityConverter() {
        return new TaskModelToEntityConverter();
    }

    @Bean
    public TaskEntityToModelConverter taskEntityToModelConverter() {
        return new TaskEntityToModelConverter();
    }

    @Bean
    public TaskEntityToDtoConverter taskEntityToDtoConverter() {
        return new TaskEntityToDtoConverter();
    }

    @Bean
    public TaskDtoToEntityConverter taskDtoToEntityConverter() {
        return new TaskDtoToEntityConverter();
    }


    // UserAccount
    @Bean
    public UserAccountModelToEntityConverter userAccountModelToEntityConverter() {
        return new UserAccountModelToEntityConverter(roleModelToEntityConverter());
    }

    @Bean
    public UserAccountEntityToModelConverter userAccountEntityToModelConverter() {
        return new UserAccountEntityToModelConverter(roleEntityToModelConverter());
    }

    @Bean
    public UserAccountEntityToDtoConverter userAccountEntityToDtoConverter() {
        return new UserAccountEntityToDtoConverter(roleEntityToDtoConverter());
    }

    @Bean
    public UserAccountDtoToEntityConverter userAccountDtoToEntityConverter() {
        return new UserAccountDtoToEntityConverter(roleDtoToEntityConverter());
    }


    // Role
    @Bean
    public RoleModelToEntityConverter roleModelToEntityConverter() {
        return new RoleModelToEntityConverter();
    }

    @Bean
    public RoleEntityToModelConverter roleEntityToModelConverter() {
        return new RoleEntityToModelConverter();
    }

    @Bean
    public RoleEntityToDtoConverter roleEntityToDtoConverter() {
        return new RoleEntityToDtoConverter();
    }

    @Bean
    public RoleDtoToEntityConverter roleDtoToEntityConverter() {
        return new RoleDtoToEntityConverter();
    }

}
