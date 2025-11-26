package com.sebastien.taskmanager.controller;

import com.sebastien.taskmanager.dto.role.RoleDTO;
import com.sebastien.taskmanager.dto.useraccount.UserAccountDTO;
import com.sebastien.taskmanager.enums.RoleEnum;
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
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    void protectedEndpointShouldReturn401WithoutToken() throws Exception {
        mockMvc.perform(get(URL))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getByIdTest() throws Exception {
        UserAccountModel userAccountModelProvided = createUserAccount();

        List<UserAccountModel> all = userAccountRepository.findAll();
        userAccountModelProvided = userAccountRepository.save(userAccountModelProvided);

        final String token = generateToken();

        final MockHttpServletRequestBuilder url = MockMvcRequestBuilders.get(URL + "/" + userAccountModelProvided.getId())
                .header("Authorization", "Bearer " + token);

        final RoleDTO roleDTOExpected = new RoleDTO();
        roleDTOExpected.setName(RoleEnum.USER);

        final UserAccountDTO userAccountDTOExpected = new UserAccountDTO();
        userAccountDTOExpected.setRoles(List.of(roleDTOExpected));
        userAccountDTOExpected.setUsername("name@email.com");

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
        roleModelProvided.setName(RoleEnum.USER);
        roleRepository.save(roleModelProvided);

        final UserAccountModel userAccountModel1Provided = createUserAccount();
        userAccountModel1Provided.setRoles(new HashSet<>(List.of(roleModelProvided)));
        userAccountRepository.save(userAccountModel1Provided);

        final UserAccountModel userAccountModel2Provided = createUserAccount();
        userAccountModel2Provided.setUsername("name2@email.com");
        userAccountModel2Provided.setRoles(new HashSet<>(List.of(roleModelProvided)));

        userAccountRepository.save(userAccountModel2Provided);

        final String token = generateToken();

        final MockHttpServletRequestBuilder url = MockMvcRequestBuilders.get(URL)
                .header("Authorization", "Bearer " + token);

        mockMvc.perform(url)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[*].username", containsInAnyOrder("name@email.com", "name2@email.com", "test@example.com")));
    }

    @Test
    void createUserAccountTest() throws Exception {
        final String userAccountJson = """
                {
                    "username": "name1@email.com",
                    "password": "pass",
                    "roles" : [
                        {
                            "name": "ADMIN"
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

        final UserAccountModel userAccountModel = userAccountRepository.findByUsername("name1@email.com").orElseThrow();

        assertThat(userAccountModel.getUsername()).isEqualTo("name1@email.com");
        assertThat(userAccountModel.getPassword()).isNotEqualTo("pass");
        assertThat(userAccountModel.getRoles()).hasSize(1);
    }

    @Test
    void createUserAccountTest_UserAccountUsernameAlreadyExist() throws Exception {
        RoleModel roleModelProvided = new RoleModel();
        roleModelProvided.setName(RoleEnum.USER);
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
                userAccountModelProvided.getRoles().stream().findFirst().map(RoleModel::getName).orElse(RoleEnum.ADMIN));

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
        roleModelProvided.setName(RoleEnum.USER);
        roleModelProvided = roleRepository.save(roleModelProvided);

        UserAccountModel userAccountModelProvided = createUserAccount();
        userAccountModelProvided.setRoles(new HashSet<>(List.of(roleModelProvided)));

        userAccountModelProvided = userAccountRepository.save(userAccountModelProvided);

        final String userAccountJson = """
                {
                    "id": %d,
                    "username": "NewName@email.com",
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
                userAccountModelProvided.getRoles().stream().findFirst().map(RoleModel::getName).orElse(null));

        final String token = generateToken();

        final MockHttpServletRequestBuilder url = MockMvcRequestBuilders.put(URL + "/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userAccountJsonFormatted)
                .header("Authorization", "Bearer " + token);

        mockMvc.perform(url)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNumber());

        final UserAccountModel userAccountModel = userAccountRepository.findByUsername("NewName@email.com").orElseThrow();
        assertThat(userAccountModel.getUsername()).isEqualTo("NewName@email.com");
        assertThat(userAccountModel.getPassword()).isNotEqualTo("NewPass");
    }

    @Test
    void updateUserAccountTest_UserAccountDoesNotExist() throws Exception {
        final String userAccountJson = """
                {
                    "id": 1,
                    "username": "NewName@email.com",
                    "password": "NewPass",
                    "roles" : [
                        {
                            "name": "USER"
                        }
                    ]
                }
                """;

        final String token = generateToken();

        final MockHttpServletRequestBuilder url = MockMvcRequestBuilders.put(URL + "/update")
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

        final MockHttpServletRequestBuilder url = MockMvcRequestBuilders.delete(URL + "/delete/" + userAccountModelProvided.getId())
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

        final MockHttpServletRequestBuilder url = MockMvcRequestBuilders.delete(URL + "/delete/9999")
                .header("Authorization", "Bearer " + token);

        mockMvc.perform(url)
                .andDo(print())
                .andExpect(status().isForbidden());

    }

    private UserAccountModel createUserAccount() {
        final UserAccountModel userAccountModel = new UserAccountModel();
        userAccountModel.setPassword(passwordEncoder.encode("pass"));
        userAccountModel.setUsername("name@email.com");

        return userAccountModel;
    }
}
