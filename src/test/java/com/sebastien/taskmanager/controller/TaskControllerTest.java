package com.sebastien.taskmanager.controller;

import com.sebastien.taskmanager.dto.task.TaskDTO;
import com.sebastien.taskmanager.enums.Status;
import com.sebastien.taskmanager.model.TaskModel;
import com.sebastien.taskmanager.repository.TaskRepository;
import com.sebastien.taskmanager.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.modelmapper.internal.bytebuddy.matcher.ElementMatchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class TaskControllerTest extends GenericControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private JwtService jwtService;

    private final String URL = "/api/tasks";

    @Test
    void getByIdTest() throws Exception {
        TaskModel taskModelProvided = createTaskModel();

        taskModelProvided = taskRepository.save(taskModelProvided);

        final String token = generateToken();

        final MockHttpServletRequestBuilder url = MockMvcRequestBuilders.get(URL + "/" + taskModelProvided.getId())
                .header("Authorization", "Bearer " + token);

        final TaskDTO taskDTOExpected = new TaskDTO();
        taskDTOExpected.setTitle("Title");
        taskDTOExpected.setDescription("Description");
        taskDTOExpected.setStatus(Status.TODO);
        taskDTOExpected.setCreatedAt(LocalDateTime.of(2025, 11, 20, 11, 34, 28));

        mockMvc.perform(url)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.title").value(taskDTOExpected.getTitle()))
                .andExpect(jsonPath("$.description").value(taskDTOExpected.getDescription()))
                .andExpect(jsonPath("$.status").value(taskDTOExpected.getStatus().toString()))
                .andExpect(jsonPath("$.createdAt", is(LocalDateTime.of(2025, 11, 20, 11, 34, 28))).exists());
    }

    @Test
    void getByIdTest_TaskNotFound() throws Exception {
        final String token = generateToken();

        final MockHttpServletRequestBuilder url = MockMvcRequestBuilders.get(URL + "/9999")
                .header("Authorization", "Bearer " + token);

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

        final String token = generateToken();

        final MockHttpServletRequestBuilder url = MockMvcRequestBuilders.get(URL)
                .header("Authorization", "Bearer " + token);

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
                    "createdAt": "2025-11-17T17:34:28.000",
                    "status": "TODO"
                }
                """;

        final String token = generateToken();

        final MockHttpServletRequestBuilder url = MockMvcRequestBuilders.post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(taskJson)
                .header("Authorization", "Bearer " + token);

        mockMvc.perform(url)
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
                    "createdAt": "%s"
                }
                """;

        final String token = generateToken();

        final MockHttpServletRequestBuilder url = MockMvcRequestBuilders.put(URL + "/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format(taskJson, taskModelProvided.getId(), taskModelProvided.getCreatedAt()))
                .header("Authorization", "Bearer " + token);

        mockMvc.perform(url)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNumber());

        final TaskModel taskModel = taskRepository.findById(taskModelProvided.getId()).orElseThrow();
        assertThat(taskModel).isNotNull();
        assertThat(taskModel.getId()).isNotNull();
        assertThat(taskModel.getTitle()).isEqualTo("Task 1");
        assertThat(taskModel.getDescription()).isEqualTo("Ceci est la task numero 1");
        assertThat(taskModel.getCreatedAt()).isNotNull();
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

        final String token = generateToken();

        final MockHttpServletRequestBuilder url = MockMvcRequestBuilders.put(URL + "/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(taskJson)
                .header("Authorization", "Bearer " + token);

        mockMvc.perform(url)
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteTaskTest() throws Exception {
        TaskModel taskModelProvided = createTaskModel();

        taskModelProvided = taskRepository.save(taskModelProvided);

        final String token = generateToken();

        final MockHttpServletRequestBuilder url = MockMvcRequestBuilders.delete(URL + "/delete/" + taskModelProvided.getId())
                .header("Authorization", "Bearer " + token);

        mockMvc.perform(url)
                .andDo(print())
                .andExpect(status().isOk());

        Optional<TaskModel> taskModelOptional = taskRepository.findById(taskModelProvided.getId());
        assertThat(taskModelOptional).isEmpty();
    }

    @Test
    void deleteTaskTest_TaskDoesNotExist() throws Exception {
        final String token = generateToken();

        final MockHttpServletRequestBuilder url = MockMvcRequestBuilders.delete(URL + "/delete/9999")
                .header("Authorization", "Bearer " + token);

        mockMvc.perform(url)
                .andDo(print())
                .andExpect(status().isBadRequest());

    }

    private TaskModel createTaskModel() {
        final TaskModel taskModel = new TaskModel();
        taskModel.setTitle("Title");
        taskModel.setDescription("Description");
        taskModel.setStatus(Status.TODO);
        taskModel.setCreatedAt(LocalDateTime.of(2025, 11, 20, 11, 34, 28));

        return taskModel;
    }
}
