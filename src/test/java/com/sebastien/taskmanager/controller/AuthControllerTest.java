package com.sebastien.taskmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sebastien.taskmanager.model.RoleModel;
import com.sebastien.taskmanager.model.UserAccountModel;
import com.sebastien.taskmanager.repository.RoleRepository;
import com.sebastien.taskmanager.repository.UserAccountRepository;
import com.sebastien.taskmanager.security.AuthRequest;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
@Transactional
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void loginShouldReturnJwtToken() throws Exception {
        final UserAccountModel userAccountModel = createUserAccountModel();

        AuthRequest req = new AuthRequest("test@example.com", "password");
        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    public void pingpongTest() throws Exception {
        mockMvc.perform(get("/auth/ping")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    private UserAccountModel createUserAccountModel() {
        RoleModel roleModel = new RoleModel();
        roleModel.setName("USER");

        roleModel = roleRepository.save(roleModel);

        final UserAccountModel userAccountModel = new UserAccountModel();
        userAccountModel.setUsername("test@example.com");
        userAccountModel.setPassword(passwordEncoder.encode("password"));
        userAccountModel.setRoles(new HashSet<>(List.of(roleModel)));

        userAccountRepository.save(userAccountModel);

        return userAccountModel;
    }
}
