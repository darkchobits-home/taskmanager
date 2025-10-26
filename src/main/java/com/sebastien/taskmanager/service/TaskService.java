package com.sebastien.taskmanager.service;

import com.sebastien.taskmanager.converter.task.TaskDtoToEntityConverter;
import com.sebastien.taskmanager.converter.task.TaskEntityToDtoConverter;
import com.sebastien.taskmanager.converter.task.TaskEntityToModelConverter;
import com.sebastien.taskmanager.converter.task.TaskModelToEntityConverter;
import com.sebastien.taskmanager.entity.task.Task;
import com.sebastien.taskmanager.model.TaskModel;
import com.sebastien.taskmanager.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskModelToEntityConverter taskModelToEntityConverter;

    @Autowired
    private TaskEntityToModelConverter taskEntityToModelConverter;

    @Autowired
    private TaskEntityToDtoConverter taskEntityToDtoConverter;

    @Autowired
    private TaskDtoToEntityConverter taskDtoToEntityConverter;

    public TaskService() {
    }

    public Optional<Long> createTask(Task task){

        return save(task);
    }

    public void delete(final Long taskId) {
        final Optional<TaskModel> existingTask = taskRepository.findById(taskId);
        existingTask.ifPresent(taskModel -> taskRepository.delete(taskModel));
    }

    public Set<Task> getAll() {
        final List<TaskModel> allTaskModels = taskRepository.findAll();

        return allTaskModels.stream().map(currentTaskModel -> this.taskModelToEntityConverter.convertModelToEntity(currentTaskModel)).collect(Collectors.toSet());
    }

    public Optional<Task> getById(final Long taskId) {
        final Optional<TaskModel> existingTaskModel = taskRepository.findById(taskId);

        return existingTaskModel.map(taskModel -> this.taskModelToEntityConverter.convertModelToEntity(taskModel));
    }

    public Optional<Long> save(final Task task) {
        final TaskModel taskModel = taskEntityToModelConverter.convertEntityToModel(task);
        final TaskModel taskModelSaved = taskRepository.save(taskModel);

        return Optional.of(taskModelSaved.getId());
    }

    public Optional<Long> updateTask(final Task task) {
        final Optional<TaskModel> existingTask = taskRepository.findById(task.getId());

        if (existingTask.isPresent()) {
            final TaskModel taskModelToUpdate = taskEntityToModelConverter.convertEntityToModel(task);
            taskModelToUpdate.setId(existingTask.get().getId());

            final TaskModel taskModelSaved = taskRepository.save(taskModelToUpdate);

            return Optional.of(taskModelSaved.getId());
        }

        return Optional.empty();
    }

}
