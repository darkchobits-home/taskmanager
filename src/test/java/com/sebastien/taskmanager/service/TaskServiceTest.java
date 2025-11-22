package com.sebastien.taskmanager.service;

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

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@TestPropertySource(locations = "classpath:application-test.yml")
public class TaskServiceTest {

    @Autowired
    private TaskService taskService;

    @MockitoBean
    private TaskRepository taskRepository;

    @Test
    public void testGetById_IdDoesNotExist() {

        Mockito.when(taskRepository.findById(1L)).thenThrow(new EntityNotFoundException("Task not found"));

        final Exception exceptionResult = assertThrows(EntityNotFoundException.class, () -> taskService.getById(1L));

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
        taskProvided.setCreatedAt(LocalDateTime.of(2025, 11, 20, 11, 34, 28));
        taskProvided.setUpdatedAt(LocalDateTime.now());
        taskProvided.setStatus(Status.IN_PROGRESS);

        // Then
        final TaskException taskExceptionResult = assertThrows(TaskException.class, () -> taskService.updateTask(taskProvided));

        // Assert
        assertThat(taskExceptionResult).isNotNull();
        assertThat(taskExceptionResult.getTaskExceptionCode()).isSameAs(TaskExceptionCode.TASK_ID_DOES_NOT_EXIST);
    }
}

