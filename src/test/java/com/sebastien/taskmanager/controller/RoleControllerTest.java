package com.sebastien.taskmanager.controller;

import com.sebastien.taskmanager.dto.role.RoleDTO;
import com.sebastien.taskmanager.model.RoleModel;
import com.sebastien.taskmanager.repository.RoleRepository;
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
public class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RoleRepository roleRepository;

    private final String URL = "/api/roles";

    @BeforeEach
    void setup() {
        roleRepository.deleteAll();
    }

    @Test
    void getByIdTest() throws Exception {
        RoleModel roleModelProvided = new RoleModel();
        roleModelProvided.setName("USER");

        roleModelProvided = roleRepository.save(roleModelProvided);

        final MockHttpServletRequestBuilder url = MockMvcRequestBuilders.get(URL + "/" + roleModelProvided.getId());

        final RoleDTO roleDTO = new RoleDTO();
        roleDTO.setName("USER");

        mockMvc.perform(url)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value(roleDTO.getName()));
    }

    @Test
    void getByIdTest_RoleNotFound() throws Exception {
        final MockHttpServletRequestBuilder url = MockMvcRequestBuilders.get(URL + "/9999");

        mockMvc.perform(url)
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllTest() throws Exception {
        final RoleModel roleModel1Provided = new RoleModel();
        roleModel1Provided.setName("USER");

        roleRepository.save(roleModel1Provided);

        final RoleModel roleModel2Provided = new RoleModel();
        roleModel2Provided.setName("ADMIN");

        roleRepository.save(roleModel2Provided);

        final MockHttpServletRequestBuilder url = MockMvcRequestBuilders.get("/api/roles");

        mockMvc.perform(url)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("USER", "ADMIN")));
    }

    @Test
    void createRoleTest() throws Exception {
        final String roleJson = """
                {
                    "name": "MANAGER"
                }
                """;

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(roleJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNumber());

        final RoleModel role = roleRepository.findByName("MANAGER").orElseThrow();
        assertThat(role.getName()).isEqualTo("MANAGER");
    }

    @Test
    void createRoleTest_RoleAlreadyExist() throws Exception {
        final RoleModel roleModel1Provided = new RoleModel();
        roleModel1Provided.setName("USER");

        roleRepository.save(roleModel1Provided);

        final String roleJson = """
                {
                    "name": "USER"
                }
                """;

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(roleJson))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    void updateRoleTest() throws Exception {
        RoleModel roleModel1Provided = new RoleModel();
        roleModel1Provided.setName("USER");

        roleModel1Provided = roleRepository.save(roleModel1Provided);

        final String roleJson = """
                {
                    "id": %d,
                    "name": "MANAGER"
                }
                """;

        mockMvc.perform(post(URL + "/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format(roleJson, roleModel1Provided.getId())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNumber());

        final RoleModel role = roleRepository.findByName("MANAGER").orElseThrow();
        assertThat(role.getId()).isNotNull();
        assertThat(role.getName()).isEqualTo("MANAGER");
    }

    @Test
    void updateRoleTest_RoleDoesNotExist() throws Exception {
        final String roleJson = """
                    {
                        "id": 1,
                        "name": "MANAGER"
                    }
                """;

        mockMvc.perform(post(URL + "/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(roleJson))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteRoleTest() throws Exception {
        RoleModel roleModel1Provided = new RoleModel();
        roleModel1Provided.setName("USER");

        roleModel1Provided = roleRepository.save(roleModel1Provided);

        mockMvc.perform(get(URL + "/delete/" + roleModel1Provided.getId()))
                .andDo(print())
                .andExpect(status().isOk());

        final Optional<RoleModel> roleModelOptional = roleRepository.findByName("USER");
        assertThat(roleModelOptional).isEmpty();
    }

    @Test
    void deleteRoleTest_RoleDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/roles/delete/9999"))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }
}
