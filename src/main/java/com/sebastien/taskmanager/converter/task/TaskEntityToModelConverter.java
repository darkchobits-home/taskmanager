package com.sebastien.taskmanager.converter.task;

import com.sebastien.taskmanager.entity.task.Task;
import com.sebastien.taskmanager.model.TaskModel;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TaskEntityToModelConverter {

    @Autowired
    private ModelMapper modelMapper;

    public TaskModel convertEntityToModel(Task task) {
        return modelMapper.map(task, TaskModel.class);
    }
}
