package com.sebastien.taskmanager.controller;

import com.sebastien.taskmanager.dto.role.RoleDTO;
import com.sebastien.taskmanager.dto.useraccount.UserAccountDTO;
import com.sebastien.taskmanager.model.RoleModel;
import com.sebastien.taskmanager.model.UserAccountModel;
import com.sebastien.taskmanager.repository.RoleRepository;
import com.sebastien.taskmanager.repository.UserAccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserAccountControllerTest extends GenericControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private RoleRepository roleRepository;

    private final String URL = "/api/useraccount";

    @Test
    void getByIdTest() throws Exception {
        UserAccountModel userAccountModelProvided = createUserAccount();

        List<UserAccountModel> all = userAccountRepository.findAll();
        userAccountModelProvided = userAccountRepository.save(userAccountModelProvided);

        final String token = generateToken();

        final MockHttpServletRequestBuilder url = MockMvcRequestBuilders.get(URL + "/" + userAccountModelProvided.getId())
                .header("Authorization", "Bearer " + token);

        final RoleDTO roleDTOExpected = new RoleDTO();
        roleDTOExpected.setName("USER");

        final UserAccountDTO userAccountDTOExpected = new UserAccountDTO();
        userAccountDTOExpected.setRoles(List.of(roleDTOExpected));
        userAccountDTOExpected.setUsername("name");

        mockMvc.perform(url)
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.username").value(userAccountDTOExpected.getUsername()));
    }

    @Test
    void getByIdTest_UserAccountNotFound() throws Exception {
        final String token = generateToken();

        final MockHttpServletRequestBuilder url = MockMvcRequestBuilders.get(URL + "/9999")
                .header("Authorization", "Bearer " + token);

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

        final String token = generateToken();

        final MockHttpServletRequestBuilder url = MockMvcRequestBuilders.get(URL)
                .header("Authorization", "Bearer " + token);

        mockMvc.perform(url)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[*].username", containsInAnyOrder("name", "name2", "test@example.com")));
    }

    @Test
    void createUserAccountTest() throws Exception {
        final String userAccountJson = """
                {
                    "username": "name1",
                    "password": "pass",
                    "roles" : [
                        {
                            "name": "USER"
                        }
                    ]
                }
                """;

        final String token = generateToken();

        final MockHttpServletRequestBuilder url = MockMvcRequestBuilders.post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(userAccountJson)
                .header("Authorization", "Bearer " + token);

        mockMvc.perform(url)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNumber());

        final UserAccountModel userAccountModel = userAccountRepository.findByUsername("name1").orElseThrow();

        assertThat(userAccountModel.getUsername()).isEqualTo("name1");
        assertThat(userAccountModel.getPassword()).isNotEqualTo("pass");
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

        final String token = generateToken();

        final MockHttpServletRequestBuilder url = MockMvcRequestBuilders.post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(userAccountJsonFormatted)
                .header("Authorization", "Bearer " + token);

        mockMvc.perform(url)
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
                            "name": "%s"
                        }
                    ]
                }
                """;

        final String userAccountJsonFormatted = String.format(userAccountJson,
                userAccountModelProvided.getId(),
                userAccountModelProvided.getRoles().stream().findFirst().map(RoleModel::getId).orElse(null),
                userAccountModelProvided.getRoles().stream().findFirst().map(RoleModel::getName).orElse(""));

        final String token = generateToken();

        final MockHttpServletRequestBuilder url = MockMvcRequestBuilders.post(URL + "/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userAccountJsonFormatted)
                .header("Authorization", "Bearer " + token);

        mockMvc.perform(url)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNumber());

        final UserAccountModel userAccountModel = userAccountRepository.findByUsername("NewName").orElseThrow();
        assertThat(userAccountModel.getUsername()).isEqualTo("NewName");
        assertThat(userAccountModel.getPassword()).isNotEqualTo("NewPass");
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

        final String token = generateToken();

        final MockHttpServletRequestBuilder url = MockMvcRequestBuilders.post(URL + "/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userAccountJson)
                .header("Authorization", "Bearer " + token);

        mockMvc.perform(url)
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteUserAccountTest() throws Exception {
        UserAccountModel userAccountModelProvided = createUserAccount();

        userAccountModelProvided = userAccountRepository.save(userAccountModelProvided);

        final String token = generateToken();

        final MockHttpServletRequestBuilder url = MockMvcRequestBuilders.get(URL + "/delete/" + userAccountModelProvided.getId())
                .header("Authorization", "Bearer " + token);

        mockMvc.perform(url)
                .andDo(print())
                .andExpect(status().isOk());

        final Optional<UserAccountModel> userAccountModelOptional = userAccountRepository.findByUsername(userAccountModelProvided.getUsername());
        assertThat(userAccountModelOptional).isEmpty();
    }

    @Test
    void deleteUserAccountTest_UserAccountDoesNotExist() throws Exception {
        final String token = generateToken();

        final MockHttpServletRequestBuilder url = MockMvcRequestBuilders.get(URL + "/delete/9999")
                .header("Authorization", "Bearer " + token);

        mockMvc.perform(url)
                .andDo(print())
                .andExpect(status().isForbidden());

    }

    private UserAccountModel createUserAccount() {
        RoleModel roleModel = new RoleModel();
        roleModel.setName("USER");
        roleModel = roleRepository.save(roleModel);

        UserAccountModel userAccountModel = new UserAccountModel();
        userAccountModel.setRoles(new HashSet<>(List.of(roleModel)));
        userAccountModel.setPassword(passwordEncoder.encode("pass"));
        userAccountModel.setUsername("name");

        return userAccountModel;
    }
}
