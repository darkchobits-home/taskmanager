package com.sebastien.taskmanager.controller;

import com.sebastien.taskmanager.dto.role.RoleDTO;
import com.sebastien.taskmanager.dto.useraccount.UserAccountDTO;
import com.sebastien.taskmanager.model.RoleModel;
import com.sebastien.taskmanager.model.UserAccountModel;
import com.sebastien.taskmanager.repository.RoleRepository;
import com.sebastien.taskmanager.repository.UserAccountRepository;
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

import java.util.HashSet;
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
public class UserAccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private RoleRepository roleRepository;

    private final String URL = "/api/useraccount";

    @BeforeEach
    void setup() {
        userAccountRepository.deleteAll();
    }

    @Test
    void getByIdTest() throws Exception {
        UserAccountModel userAccountModelProvided = createUserAccount();

        userAccountModelProvided = userAccountRepository.save(userAccountModelProvided);

        final MockHttpServletRequestBuilder url = MockMvcRequestBuilders.get(URL + "/" + userAccountModelProvided.getId());

        final RoleDTO roleDTOExpected = new RoleDTO();
        roleDTOExpected.setId(1L);
        roleDTOExpected.setName("USER");

        final UserAccountDTO userAccountDTOExpected = new UserAccountDTO();
        userAccountDTOExpected.setRoles(List.of(roleDTOExpected));
        userAccountDTOExpected.setPassword("pass");
        userAccountDTOExpected.setEmail("mail");
        userAccountDTOExpected.setUsername("name");

        mockMvc.perform(url)
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.password").value(userAccountDTOExpected.getPassword()))
                .andExpect(jsonPath("$.email").value(userAccountDTOExpected.getEmail()))
                .andExpect(jsonPath("$.username").value(userAccountDTOExpected.getUsername()));
    }

    @Test
    void getByIdTest_UserAccountNotFound() throws Exception {
        final MockHttpServletRequestBuilder url = MockMvcRequestBuilders.get(URL + "/999999999");

        mockMvc.perform(url)
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllTest() throws Exception {
        final RoleModel roleModelProvided = new RoleModel();
        roleModelProvided.setName("USER");
        roleRepository.save(roleModelProvided);

        final UserAccountModel userAccountModel1Provided = createUserAccount();
        userAccountModel1Provided.setRoles(new HashSet<>(List.of(roleModelProvided)));
        userAccountRepository.save(userAccountModel1Provided);

        final UserAccountModel userAccountModel2Provided = createUserAccount();
        userAccountModel2Provided.setUsername("name2");
        userAccountModel2Provided.setRoles(new HashSet<>(List.of(roleModelProvided)));

        userAccountRepository.save(userAccountModel2Provided);

        final MockHttpServletRequestBuilder url = MockMvcRequestBuilders.get(URL);

        mockMvc.perform(url)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[*].username", containsInAnyOrder("name", "name2")));
    }

    @Test
    void createUserAccountTest() throws Exception {
        final String userAccountJson = """
                {
                    "username": "name1",
                    "password": "pass",
                    "roles" : [
                        {
                            "id": %d,
                            "name": "USER"
                        }
                    ]
                }
                """;

        RoleModel roleModelProvided = new RoleModel();
        roleModelProvided.setName("USER");
        roleModelProvided = roleRepository.save(roleModelProvided);

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format(userAccountJson, roleModelProvided.getId())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNumber());

        final UserAccountModel userAccountModel = userAccountRepository.findByUsername("name1").orElseThrow();

        assertThat(userAccountModel.getUsername()).isEqualTo("name1");
        assertThat(userAccountModel.getPassword()).isEqualTo("pass");
        assertThat(userAccountModel.getEmail()).isNullOrEmpty();
        assertThat(userAccountModel.getRoles()).hasSize(1);
    }

    @Test
    void createUserAccountTest_UserAccountUsernameAlreadyExist() throws Exception {
        RoleModel roleModelProvided = new RoleModel();
        roleModelProvided.setName("USER");
        roleModelProvided = roleRepository.save(roleModelProvided);

        final UserAccountModel userAccountModelProvided = createUserAccount();
        userAccountModelProvided.setRoles(new HashSet<>(List.of(roleModelProvided)));

        userAccountRepository.save(userAccountModelProvided);

        final String userAccountJson = """
                {
                    "username": "%s",
                    "password": "%s",
                    "roles" : [
                        {
                            "id": %d,
                            "name": "%s"
                        }
                    ]
                }
                """;

        final String userAccountJsonFormatted = String.format(userAccountJson,
                userAccountModelProvided.getUsername(),
                userAccountModelProvided.getPassword(),
                userAccountModelProvided.getRoles().stream().findFirst().map(RoleModel::getId).orElse(null),
                userAccountModelProvided.getRoles().stream().findFirst().map(RoleModel::getName).orElse(""));

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userAccountJsonFormatted))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    void updateUserAccountTest() throws Exception {
        RoleModel roleModelProvided = new RoleModel();
        roleModelProvided.setName("USER");
        roleModelProvided = roleRepository.save(roleModelProvided);

        UserAccountModel userAccountModelProvided = createUserAccount();
        userAccountModelProvided.setRoles(new HashSet<>(List.of(roleModelProvided)));

        userAccountModelProvided = userAccountRepository.save(userAccountModelProvided);

        final String userAccountJson = """
                {
                    "id": %d,
                    "username": "NewName",
                    "password": "NewPass",
                    "roles" : [
                        {
                            "id": %d,
                            "name": "%s"
                        }
                    ]
                }
                """;

        final String userAccountJsonFormatted = String.format(userAccountJson,
                userAccountModelProvided.getId(),
                userAccountModelProvided.getRoles().stream().findFirst().map(RoleModel::getId).orElse(null),
                userAccountModelProvided.getRoles().stream().findFirst().map(RoleModel::getName).orElse(""));

        mockMvc.perform(post(URL + "/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userAccountJsonFormatted))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNumber());

        final UserAccountModel userAccountModel = userAccountRepository.findByUsername("NewName").orElseThrow();
        assertThat(userAccountModel.getUsername()).isEqualTo("NewName");
        assertThat(userAccountModel.getPassword()).isEqualTo("NewPass");
        assertThat(userAccountModel.getEmail()).isNullOrEmpty();
    }

    @Test
    void updateUserAccountTest_UserAccountDoesNotExist() throws Exception {
        final String userAccountJson = """
                {
                    "id": 1,
                    "username": "NewName",
                    "password": "NewPass",
                    "roles" : [
                        {
                            "id": 1,
                            "name": "USER"
                        }
                    ]
                }
                """;

        mockMvc.perform(post(URL + "/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userAccountJson))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteUserAccountTest() throws Exception {
        UserAccountModel userAccountModelProvided = createUserAccount();

        userAccountModelProvided = userAccountRepository.save(userAccountModelProvided);

        mockMvc.perform(get(URL + "/delete/" + userAccountModelProvided.getId()))
                .andDo(print())
                .andExpect(status().isOk());

        final Optional<UserAccountModel> userAccountModelOptional = userAccountRepository.findByUsername(userAccountModelProvided.getUsername());
        assertThat(userAccountModelOptional).isEmpty();
    }

    @Test
    void deleteUserAccountTest_UserAccountDoesNotExist() throws Exception {
        mockMvc.perform(get(URL + "/delete/9999"))
                .andDo(print())
                .andExpect(status().isForbidden());

    }

    private UserAccountModel createUserAccount() {
        final RoleModel roleModel = new RoleModel();
        roleModel.setId(1L);
        roleModel.setName("USER");

        UserAccountModel userAccountModel = new UserAccountModel();
        userAccountModel.setRoles(new HashSet<>(List.of(roleModel)));
        userAccountModel.setPassword("pass");
        userAccountModel.setEmail("mail");
        userAccountModel.setUsername("name");

        return userAccountModel;
    }
}
