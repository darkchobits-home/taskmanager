package com.sebastien.taskmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sebastien.taskmanager.model.UserAccountModel;
import com.sebastien.taskmanager.repository.UserAccountRepository;
import com.sebastien.taskmanager.security.AuthRequest;
import com.sebastien.taskmanager.security.RefreshTokenRequest;
import com.sebastien.taskmanager.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthControllerTest extends GenericControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private JwtService jwtService;

    @Test
    void loginShouldReturnJwtToken() throws Exception {
        final AuthRequest req = new AuthRequest("test@example.com", "password");
        final ObjectMapper objectMapper = new ObjectMapper();

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists());

        UserAccountModel userAccountModel = userAccountRepository.findByUsername("test@example.com").orElse(null);
        assertThat(userAccountModel).isNotNull();
        assertThat(userAccountModel.getRefreshToken()).isNotNull();
    }

    @Test
    void refreshShouldReturnJwtToken() throws Exception {

        final UserAccountModel userAccountModel = userAccountRepository.findByUsername("test@example.com").orElseThrow();

        final UserDetails userDetails = new User(userAccountModel.getUsername(),
                userAccountModel.getPassword(),
                userAccountModel.getRoles().stream()
                        .map(roleModel -> new SimpleGrantedAuthority("ROLE_" + roleModel.getName()))
                        .collect(Collectors.toList()));

        final String refreshToken = jwtService.generateRefreshToken(userDetails);

        final RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest(refreshToken);

        final ObjectMapper objectMapper = new ObjectMapper();

        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshTokenRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andDo(print());
    }
}
