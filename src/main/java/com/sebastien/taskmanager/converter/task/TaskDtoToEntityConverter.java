package com.sebastien.taskmanager.converter.task;

import com.sebastien.taskmanager.dto.task.TaskCreationDTO;
import com.sebastien.taskmanager.dto.task.TaskDTO;
import com.sebastien.taskmanager.dto.task.TaskUpdateDTO;
import com.sebastien.taskmanager.entity.task.Task;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

public class TaskDtoToEntityConverter {

    @Autowired
    private ModelMapper modelMapper;

    public Task convertCreationDtoToEntity(TaskCreationDTO taskCreationDTO) {
        return modelMapper.map(taskCreationDTO, Task.class);
    }

    public Task convertUpdateDtoToEntity(TaskUpdateDTO taskUpdateDTO) {
        return modelMapper.map(taskUpdateDTO, Task.class);
    }

    public Task convertDtoToEntity(TaskDTO taskDTO) {
        return modelMapper.map(taskDTO, Task.class);
    }
}
