package com.sebastien.taskmanager.service;

import com.sebastien.taskmanager.TaskmanagerApplication;
import com.sebastien.taskmanager.entity.task.Task;
import com.sebastien.taskmanager.enums.Status;
import com.sebastien.taskmanager.exceptions.TaskException;
import com.sebastien.taskmanager.exceptions.TaskExceptionCode;
import com.sebastien.taskmanager.repository.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = TaskmanagerApplication.class)
@TestPropertySource(locations = "classpath:application-test.yml")
public class TaskServiceTest {

    @Autowired
    private TaskService taskService;

    @MockitoBean
    private TaskRepository taskRepository;

    @Test
    public void testGetById_IdDoesNotExist() {
        // When
        //final TaskException taskException = new TaskException(TaskExceptionCode.TASK_ID_DOES_NOT_EXIST);

        Mockito.when(taskRepository.findById(1L)).thenThrow(new EntityNotFoundException("Task not found"));

        // Then
        final Exception exceptionResult = assertThrows(EntityNotFoundException.class, () -> taskService.getById(1L));

        // Asserts
        assertThat(exceptionResult).isNotNull();
        assertThat(exceptionResult).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    public void testUpdateTask_TaskIdDoesNotExist() {
        // When
        Mockito.when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        final Task taskProvided = new Task();
        taskProvided.setId(1L);
        taskProvided.setTitle("Task title");
        taskProvided.setDescription("Task description");
        taskProvided.setDueDate(LocalDate.now());
        taskProvided.setStatus(Status.IN_PROGRESS);

        // Then
        final TaskException taskExceptionResult = assertThrows(TaskException.class, () -> taskService.updateTask(taskProvided));

        // Assert
        assertThat(taskExceptionResult).isNotNull();
        assertThat(taskExceptionResult.getTaskExceptionCode()).isSameAs(TaskExceptionCode.TASK_ID_DOES_NOT_EXIST);
    }
}
