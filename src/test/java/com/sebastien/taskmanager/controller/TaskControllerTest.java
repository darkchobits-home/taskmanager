package com.sebastien.taskmanager.controller;

import com.sebastien.taskmanager.dto.task.TaskDTO;
import com.sebastien.taskmanager.enums.Status;
import com.sebastien.taskmanager.model.TaskModel;
import com.sebastien.taskmanager.repository.TaskRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
@Transactional
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    private final String URL = "/api/tasks";

    @BeforeEach
    void setup() {
        taskRepository.deleteAll();
    }

    @Test
    void getByIdTest() throws Exception {
        TaskModel taskModelProvided = createTaskModel();

        taskModelProvided = taskRepository.save(taskModelProvided);

        final MockHttpServletRequestBuilder url = MockMvcRequestBuilders.get(URL + "/" + taskModelProvided.getId());

        final TaskDTO taskDTOExpected = new TaskDTO();
        taskDTOExpected.setTitle("Title");
        taskDTOExpected.setDescription("Description");
        taskDTOExpected.setStatus(Status.CREATED);
        taskDTOExpected.setDueDate(LocalDate.now());

        mockMvc.perform(url)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.title").value(taskDTOExpected.getTitle()))
                .andExpect(jsonPath("$.description").value(taskDTOExpected.getDescription()))
                .andExpect(jsonPath("$.status").value(taskDTOExpected.getStatus().toString()))
                .andExpect(jsonPath("$.dueDate").isNotEmpty());
    }

    @Test
    void getByIdTest_TaskNotFound() throws Exception {
        final MockHttpServletRequestBuilder url = MockMvcRequestBuilders.get(URL + "/9999");

        mockMvc.perform(url)
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllTest() throws Exception {
        final TaskModel taskModel1Provided = createTaskModel();

        taskRepository.save(taskModel1Provided);

        final TaskModel taskModel2Provided = createTaskModel();
        taskModel2Provided.setTitle("title 2");

        taskRepository.save(taskModel2Provided);

        final MockHttpServletRequestBuilder url = MockMvcRequestBuilders.get(URL);

        mockMvc.perform(url)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[*].title", containsInAnyOrder("Title", "title 2")));
    }

    @Test
    void createTaskTest() throws Exception {
        final String taskJson = """
                {
                    "title": "Task 1",
                    "description": "Ceci est la task numero 1",
                    "dueDate": "2025-11-17"
                }
                """;

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNumber());

        List<TaskModel> taskModelList = taskRepository.findAll();
        assertThat(taskModelList).hasSize(1);
        assertThat(taskModelList.get(0).getTitle()).isEqualTo("Task 1");
    }

    @Test
    void updateTaskTest() throws Exception {
        TaskModel taskModelProvided = createTaskModel();

        taskModelProvided = taskRepository.save(taskModelProvided);

        final String taskJson = """
                {
                    "id": %d,
                    "title": "Task 1",
                    "description": "Ceci est la task numero 1",
                    "dueDate": "2025-11-17"
                }
                """;

        mockMvc.perform(post(URL + "/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format(taskJson, taskModelProvided.getId())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNumber());

        final TaskModel taskModel = taskRepository.findById(taskModelProvided.getId()).orElseThrow();
        assertThat(taskModel.getId()).isNotNull();
        assertThat(taskModel.getTitle()).isEqualTo("Task 1");
        assertThat(taskModel.getDescription()).isEqualTo("Ceci est la task numero 1");
        assertThat(taskModel.getDueDate()).isNotNull();
    }

    @Test
    void updateTaskTest_TaskDoesNotExist() throws Exception {
        final String taskJson = """
                {
                    "id": %d,
                    "title": "Task 1",
                    "description": "Ceci est la task numero 1",
                    "dueDate": "2025-11-17"
                }
                """;

        mockMvc.perform(post(URL + "/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskJson))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteTaskTest() throws Exception {
        TaskModel taskModelProvided = createTaskModel();

        taskModelProvided = taskRepository.save(taskModelProvided);

        mockMvc.perform(get(URL + "/delete/" + taskModelProvided.getId()))
                .andDo(print())
                .andExpect(status().isOk());

        Optional<TaskModel> taskModelOptional = taskRepository.findById(taskModelProvided.getId());
        assertThat(taskModelOptional).isEmpty();
    }

    @Test
    void deleteTaskTest_TaskDoesNotExist() throws Exception {
        mockMvc.perform(get(URL + "/delete/9999"))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }

    private TaskModel createTaskModel() {
        final TaskModel taskModel = new TaskModel();
        taskModel.setTitle("Title");
        taskModel.setDescription("Description");
        taskModel.setStatus(Status.CREATED);
        taskModel.setDueDate(LocalDate.now());

        return taskModel;
    }
}
