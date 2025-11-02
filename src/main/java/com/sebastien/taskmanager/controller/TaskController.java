package com.sebastien.taskmanager.controller;

import com.sebastien.taskmanager.converter.task.TaskDtoToEntityConverter;
import com.sebastien.taskmanager.converter.task.TaskEntityToDtoConverter;
import com.sebastien.taskmanager.dto.task.TaskCreationDTO;
import com.sebastien.taskmanager.dto.task.TaskDTO;
import com.sebastien.taskmanager.dto.task.TaskUpdateDTO;
import com.sebastien.taskmanager.entity.task.Task;
import com.sebastien.taskmanager.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RequestMapping("/api/tasks")
@RestController
@SuppressWarnings("unused")
public class TaskController {

    private final TaskService taskService;

    @Autowired
    private TaskEntityToDtoConverter taskEntityToDtoConverter;

    @Autowired
    private TaskDtoToEntityConverter taskDtoToEntityConverter;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @Operation(summary = "Create a task", description = "Return the id of the task created.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully created."),
            @ApiResponse(responseCode = "400", description = "Error bad request, see details in logs."),
            @ApiResponse(responseCode = "500", description = "Unknown error, see details in logs.")
    })
    @PostMapping
    public ResponseEntity<Long> createTask(@RequestBody TaskCreationDTO taskCreationDTO) {
        final Task task = taskDtoToEntityConverter.convertCreationDtoToEntity(taskCreationDTO);
        final Optional<Long> taskId = taskService.createTask(task);

        return taskId.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }

    @Operation(summary = "Get all tasks", description = "Return the list of tasks.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully created."),
            @ApiResponse(responseCode = "500", description = "Unknown error, see details in logs.")
    })
    @GetMapping
    public Set<TaskDTO> getAll() {
        final Set<Task> allTasks = taskService.getAll();

        return allTasks.stream().map(task -> taskEntityToDtoConverter.convertEntityToDto(task)).collect(Collectors.toSet());
    }

    @Operation(summary = "Get a task by id", description = "Return the task with the id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully get."),
            @ApiResponse(responseCode = "500", description = "Unknown error, see details in logs.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> getById(@PathVariable @NotNull Long id) {
        final Optional<Task> taskOptional = taskService.getById(id);

        return taskOptional.map(task -> ResponseEntity.ok(taskEntityToDtoConverter.convertEntityToDto(task))).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Update a task", description = "Return the id of the task updated.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated."),
            @ApiResponse(responseCode = "500", description = "Unknown error, see details in logs.")
    })
    @PostMapping("/update")
    public ResponseEntity<Long> updateTask(@RequestBody TaskUpdateDTO taskUpdateDTO) {
        final Task task = taskDtoToEntityConverter.convertUpdateDtoToEntity(taskUpdateDTO);
        final Optional<Long> taskId = taskService.updateTask(task);

        return taskId.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }

    @Operation(summary = "Delete a task", description = "Return the id of the task deleted.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted."),
            @ApiResponse(responseCode = "500", description = "Unknown error, see details in logs.")
    })
    @GetMapping("/delete/{taskId}")
    public ResponseEntity<Long> deleteRole(@PathVariable @NotNull Long taskId) {
        taskService.deleteTask(taskId);

        return ResponseEntity.ok(taskId);
    }
}
