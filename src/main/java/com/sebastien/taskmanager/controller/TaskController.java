package com.sebastien.taskmanager.controller;

import com.sebastien.taskmanager.converter.task.TaskDtoToEntityConverter;
import com.sebastien.taskmanager.converter.task.TaskEntityToDtoConverter;
import com.sebastien.taskmanager.dto.task.TaskCreationDTO;
import com.sebastien.taskmanager.dto.task.TaskDTO;
import com.sebastien.taskmanager.dto.task.TaskUpdateDTO;
import com.sebastien.taskmanager.entity.task.Task;
import com.sebastien.taskmanager.service.TaskService;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RequestMapping("/api/tasks")
@RestController
public class TaskController {

    private final TaskService taskService;

    @Autowired
    private TaskEntityToDtoConverter taskEntityToDtoConverter;

    @Autowired
    private TaskDtoToEntityConverter taskDtoToEntityConverter;


    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public Set<TaskDTO> getAll() {
        final Set<Task> allTasks = taskService.getAll();

        return allTasks.stream().map(task -> taskEntityToDtoConverter.convertEntityToDto(task)).collect(Collectors.toSet());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> getById(@PathVariable @NotNull Long id) {
        final Optional<Task> taskOptional = taskService.getById(id);

        return taskOptional.map(task -> ResponseEntity.ok(taskEntityToDtoConverter.convertEntityToDto(task))).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Long> createTask(@RequestBody TaskCreationDTO taskCreationDTO) {
        final Task task = taskDtoToEntityConverter.convertCreationDtoToEntity(taskCreationDTO);
        final Optional<Long> taskId = taskService.createTask(task);

        return taskId.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PostMapping("/update")
    public ResponseEntity<Long> updateTask(@RequestBody TaskUpdateDTO taskUpdateDTO) {
        final Task task = taskDtoToEntityConverter.convertUpdateDtoToEntity(taskUpdateDTO);
        final Optional<Long> taskId = taskService.updateTask(task);

        return taskId.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }
}
