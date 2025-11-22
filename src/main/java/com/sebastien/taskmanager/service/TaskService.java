package com.sebastien.taskmanager.service;

import com.sebastien.taskmanager.converter.task.TaskEntityToModelConverter;
import com.sebastien.taskmanager.converter.task.TaskModelToEntityConverter;
import com.sebastien.taskmanager.entity.task.Task;
import com.sebastien.taskmanager.exceptions.TaskException;
import com.sebastien.taskmanager.exceptions.TaskExceptionCode;
import com.sebastien.taskmanager.exceptions.UserAccountException;
import com.sebastien.taskmanager.exceptions.UserAccountExceptionCode;
import com.sebastien.taskmanager.model.TaskModel;
import com.sebastien.taskmanager.model.UserAccountModel;
import com.sebastien.taskmanager.repository.TaskRepository;
import com.sebastien.taskmanager.repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TaskService {

    @Autowired
    @SuppressWarnings("unused")
    private TaskRepository taskRepository;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    @SuppressWarnings("unused")
    private TaskModelToEntityConverter taskModelToEntityConverter;

    @Autowired
    @SuppressWarnings("unused")
    private TaskEntityToModelConverter taskEntityToModelConverter;

    public TaskService() {
    }

    /**
     * Create a task in database.
     *
     * @param task The task
     * @return The id of the task created.
     */
    public Optional<Long> createTask(Task task){
        final TaskModel taskModel = taskEntityToModelConverter.convertEntityToModel(task);

        // Get UserAccountModel
        if (task.getAssignedTo() != null) {
            UserAccountModel userAccountModel = userAccountRepository.findByUsername(task.getAssignedTo().getUsername()).orElseThrow(() -> {
                final UserAccountException userAccountException = new UserAccountException(UserAccountExceptionCode.USER_ACCOUNT_NOT_FOUND);
                userAccountException.getDetails().put("Username", task.getAssignedTo().getUsername());

                return userAccountException;
            });
            taskModel.setAssignedTo(userAccountModel);
        }

        return save(taskModel);
    }

    /**
     * Get all task existing in database.
     *
     * @return A set of Task.
     */
    public Set<Task> getAll() {
        final List<TaskModel> allTaskModels = taskRepository.findAll();

        return allTaskModels.stream().map(currentTaskModel -> this.taskModelToEntityConverter.convertModelToEntity(currentTaskModel)).collect(Collectors.toSet());
    }

    /**
     * Get a Task with a specific id.
     *
     * @param taskId The id of the Task.
     * @return The Task found.
     */
    public Optional<Task> getById(final Long taskId) {
        final Optional<TaskModel> existingTaskModel = taskRepository.findById(taskId);

        return existingTaskModel.map(taskModel -> this.taskModelToEntityConverter.convertModelToEntity(taskModel));
    }

    /**
     * Update a Task.
     *
     * @param task The Task to update.
     * @return The id of the Task updated.
     */
    public Optional<Long> updateTask(final Task task) {
        TaskModel existingTask = taskRepository.findById(task.getId()).orElseThrow(() -> {
            final TaskException taskException = new TaskException(TaskExceptionCode.TASK_ID_DOES_NOT_EXIST);
            taskException.getDetails().put("TaskId", String.valueOf(task.getId()));

            return taskException;
        });

        final TaskModel taskModelToUpdate = taskEntityToModelConverter.convertEntityToModel(task);
        taskModelToUpdate.setId(existingTask.getId());

        if (existingTask.getAssignedTo() != null) {
            final String username = existingTask.getAssignedTo().getUsername();

            UserAccountModel userAccountModel = userAccountRepository.findByUsername(username).orElseThrow(() -> {
                final UserAccountException userAccountException = new UserAccountException(UserAccountExceptionCode.USER_ACCOUNT_NOT_FOUND);
                userAccountException.getDetails().put("Username", username);

                return userAccountException;
            });

            taskModelToUpdate.setAssignedTo(userAccountModel);
        }

        return save(taskModelToUpdate);
    }

    /**
     * Delete the Task with a specific id in database.
     *
     * @param taskId The id of the Task.
     */
    public void deleteTask(final Long taskId) {
        final Optional<TaskModel> existingTask = taskRepository.findById(taskId);

        if (existingTask.isEmpty()) {
            TaskException taskException = new TaskException(TaskExceptionCode.TASK_ID_DOES_NOT_EXIST);
            taskException.getDetails().put("Id", String.valueOf(taskId));

            throw taskException;
        }
        taskRepository.delete(existingTask.get());
    }

    /**
     * Save the Task in database.
     *
     * @param taskModel The Task to save.
     * @return The id of the Task saved.
     */
    public Optional<Long> save(final TaskModel taskModel) {
        final TaskModel taskModelSaved = taskRepository.save(taskModel);

        return Optional.of(taskModelSaved.getId());
    }

}
