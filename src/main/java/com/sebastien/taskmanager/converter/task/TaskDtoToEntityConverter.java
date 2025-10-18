package com.sebastien.taskmanager.converter.task;

import com.sebastien.taskmanager.dto.task.TaskCreationDTO;
import com.sebastien.taskmanager.dto.task.TaskDTO;
import com.sebastien.taskmanager.dto.task.TaskUpdateDTO;
import com.sebastien.taskmanager.entity.task.Task;
import com.sebastien.taskmanager.enums.Status;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

public class TaskDtoToEntityConverter {

    @Autowired
    private ModelMapper modelMapper;

    public Task convertCreationDtoToEntity(TaskCreationDTO taskCreationDTO) {
        Task task = modelMapper.map(taskCreationDTO, Task.class);
        task.setStatus(Status.CREATED);

        return task;
    }

    public Task convertUpdateDtoToEntity(TaskUpdateDTO taskUpdateDTO) {
        return modelMapper.map(taskUpdateDTO, Task.class);
    }

    public Task convertDtoToEntity(TaskDTO taskDTO) {
        return modelMapper.map(taskDTO, Task.class);
    }
}
