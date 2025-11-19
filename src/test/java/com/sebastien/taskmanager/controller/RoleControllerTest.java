package com.sebastien.taskmanager.controller;

import com.sebastien.taskmanager.model.RoleModel;
import com.sebastien.taskmanager.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RoleControllerTest extends GenericControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RoleRepository roleRepository;

    private final String URL = "/api/roles";

    @Test
    void protectedEndpointShouldReturn401WithoutToken() throws Exception {
        mockMvc.perform(get(URL))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getByIdTest() throws Exception {
        RoleModel roleModelProvided = new RoleModel();
        roleModelProvided.setName("USER");

        roleModelProvided = roleRepository.save(roleModelProvided);

        final String token = generateToken();

        final MockHttpServletRequestBuilder url = MockMvcRequestBuilders.get(URL + "/" + roleModelProvided.getId())
                .header("Authorization", "Bearer " + token);

        mockMvc.perform(url)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("USER"));
    }

    @Test
    void getByIdTest_RoleNotFound() throws Exception {
        final String token = generateToken();

        final MockHttpServletRequestBuilder url = MockMvcRequestBuilders.get(URL + "/9999")
                .header("Authorization", "Bearer " + token);

        mockMvc.perform(url)
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllTest() throws Exception {
        final RoleModel roleModel1Provided = new RoleModel();
        roleModel1Provided.setName("MANAGER");

        roleRepository.save(roleModel1Provided);

        final RoleModel roleModel2Provided = new RoleModel();
        roleModel2Provided.setName("USER");

        roleRepository.save(roleModel2Provided);

        final String token = generateToken();
        final MockHttpServletRequestBuilder url = MockMvcRequestBuilders.get(URL)
                .header("Authorization", "Bearer " + token);

        mockMvc.perform(url)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("USER", "MANAGER", "ADMIN")));
    }

    @Test
    void createRoleTest() throws Exception {
        final String roleJson = """
                {
                    "name": "MANAGER"
                }
                """;

        final String token = generateToken();
        final MockHttpServletRequestBuilder url = MockMvcRequestBuilders.post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(roleJson)
                .header("Authorization", "Bearer " + token);

        mockMvc.perform(url)
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

        final String token = generateToken();
        final MockHttpServletRequestBuilder url = MockMvcRequestBuilders.post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(roleJson)
                .header("Authorization", "Bearer " + token);

        mockMvc.perform(url)
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

        final String token = generateToken();
        final MockHttpServletRequestBuilder url = MockMvcRequestBuilders.put(URL + "/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format(roleJson, roleModel1Provided.getId()))
                .header("Authorization", "Bearer " + token);

        mockMvc.perform(url)
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
        final String token = generateToken();
        final MockHttpServletRequestBuilder url = MockMvcRequestBuilders.put(URL + "/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(roleJson)
                .header("Authorization", "Bearer " + token);

        mockMvc.perform(url)
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteRoleTest() throws Exception {
        RoleModel roleModel1Provided = new RoleModel();
        roleModel1Provided.setName("USER");

        roleModel1Provided = roleRepository.save(roleModel1Provided);

        final String token = generateToken();
        final MockHttpServletRequestBuilder url = MockMvcRequestBuilders.delete(URL + "/delete/" + roleModel1Provided.getId())
                .header("Authorization", "Bearer " + token);

        mockMvc.perform(url)
                .andDo(print())
                .andExpect(status().isOk());

        final Optional<RoleModel> roleModelOptional = roleRepository.findByName("USER");
        assertThat(roleModelOptional).isEmpty();
    }

    @Test
    void deleteRoleTest_RoleDoesNotExist() throws Exception {
        final String token = generateToken();
        final MockHttpServletRequestBuilder url = MockMvcRequestBuilders.delete(URL + "/delete/9999")
                .header("Authorization", "Bearer " + token);

        mockMvc.perform(url)
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

}
