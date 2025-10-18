package com.sebastien.taskmanager.converter.task;

import com.sebastien.taskmanager.dto.task.TaskDTO;
import com.sebastien.taskmanager.entity.task.Task;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TaskEntityToDtoConverter {

    @Autowired
    private ModelMapper modelMapper;

    public TaskDTO convertEntityToDto(Task task) {
        return modelMapper.map(task, TaskDTO.class);
    }
}
