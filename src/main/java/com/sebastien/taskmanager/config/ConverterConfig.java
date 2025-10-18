package com.sebastien.taskmanager.config;

import com.sebastien.taskmanager.converter.task.TaskDtoToEntityConverter;
import com.sebastien.taskmanager.converter.task.TaskEntityToDtoConverter;
import com.sebastien.taskmanager.converter.task.TaskEntityToModelConverter;
import com.sebastien.taskmanager.converter.task.TaskModelToEntityConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class ConverterConfig {

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
}
