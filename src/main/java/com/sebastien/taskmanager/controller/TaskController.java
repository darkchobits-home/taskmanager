package com.sebastien.taskmanager.controller;

import com.sebastien.taskmanager.converter.task.TaskDtoToEntityConverter;
import com.sebastien.taskmanager.converter.task.TaskEntityToDtoConverter;
import com.sebastien.taskmanager.dto.task.TaskCreationDTO;
import com.sebastien.taskmanager.dto.task.TaskDTO;
import com.sebastien.taskmanager.dto.task.TaskUpdateDTO;
import com.sebastien.taskmanager.entity.task.Task;
import com.sebastien.taskmanager.enums.Priority;
import com.sebastien.taskmanager.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<Long> createTask(@RequestBody TaskCreationDTO taskCreationDTO) {
        final Task task = taskDtoToEntityConverter.convertCreationDtoToEntity(taskCreationDTO);

        if (taskCreationDTO.getPriority() == null) {
            task.setPriority(Priority.LOW);
        }

        final Optional<Long> taskId = taskService.createTask(task);

        return taskId.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }

    @Operation(summary = "Get all tasks", description = "Return the list of tasks.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully created."),
            @ApiResponse(responseCode = "500", description = "Unknown error, see details in logs.")
    })
    @GetMapping("/list")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public Set<TaskDTO> getAll(@RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size,
                               @RequestParam(defaultValue = "id,asc") String [] sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(parseSort(sort)));

        Page<Task> allTasks = taskService.getAll(pageable);

        return allTasks.stream().map(task -> taskEntityToDtoConverter.convertEntityToDto(task)).collect(Collectors.toSet());
    }

    @Operation(summary = "Get a task by id", description = "Return the task with the id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully get."),
            @ApiResponse(responseCode = "500", description = "Unknown error, see details in logs.")
    })
    @GetMapping("/{id}")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<TaskDTO> getById(@PathVariable @NotNull Long id) {
        final Optional<Task> taskOptional = taskService.getById(id);

        return taskOptional.map(task -> ResponseEntity.ok(taskEntityToDtoConverter.convertEntityToDto(task))).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Update a task", description = "Return the id of the task updated.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated."),
            @ApiResponse(responseCode = "500", description = "Unknown error, see details in logs.")
    })
    @PutMapping("/update")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
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
    @DeleteMapping("/delete/{taskId}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<Long> deleteTask(@PathVariable @NotNull Long taskId) {
        taskService.deleteTask(taskId);

        return ResponseEntity.ok(taskId);
    }

    @Operation(summary = "Search task containing a title", description = "Return a list of Task containing a title.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully searched."),
            @ApiResponse(responseCode = "500", description = "Unknown error, see details in logs.")
    })
    @GetMapping("/search")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<Page<TaskDTO>> searchTasks(@RequestParam String title,
                                                     @RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Task> tasksFoundPage = taskService.searchByTitle(title, pageable);
        List<TaskDTO> taskDTOList = tasksFoundPage.stream().map(task -> this.taskEntityToDtoConverter.convertEntityToDto(task)).collect(Collectors.toList());

        return ResponseEntity.ok(new PageImpl<>(taskDTOList));
    }

    private Sort.Order parseSort(String[] sort) {
        if (sort.length == 2) {
            return new Sort.Order(
                    sort[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC,
                    sort[0]
            );
        }
        return new Sort.Order(Sort.Direction.ASC, "id");
    }
}
