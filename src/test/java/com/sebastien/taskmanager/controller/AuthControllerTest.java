package com.sebastien.taskmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sebastien.taskmanager.security.AuthRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthControllerTest extends GenericControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void loginShouldReturnJwtToken() throws Exception {
        final AuthRequest req = new AuthRequest("test@example.com", "password");
        final ObjectMapper objectMapper = new ObjectMapper();

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }
}
