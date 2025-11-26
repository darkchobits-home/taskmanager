package com.sebastien.taskmanager.controller;

import com.sebastien.taskmanager.enums.RoleEnum;
import com.sebastien.taskmanager.model.RoleModel;
import com.sebastien.taskmanager.model.UserAccountModel;
import com.sebastien.taskmanager.repository.RoleRepository;
import com.sebastien.taskmanager.repository.UserAccountRepository;
import com.sebastien.taskmanager.service.JwtService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@TestPropertySource(locations = "classpath:application-test.yml")
@Transactional
public class GenericControllerTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private JwtService jwtService;

    private final String USERNAME = "test@example.com";

    private final RoleEnum ROLE_ADMIN = RoleEnum.ADMIN;

    @BeforeEach
    void setup() throws SQLException {
        userAccountRepository.deleteAll();
        userAccountRepository.flush();

        roleRepository.deleteAll();
        roleRepository.flush();

        RoleModel roleModel = new RoleModel();
        roleModel.setName(ROLE_ADMIN);

        roleModel = roleRepository.save(roleModel);

        final UserAccountModel userAccountModel = new UserAccountModel();
        userAccountModel.setUsername(USERNAME);
        userAccountModel.setPassword(passwordEncoder.encode("password"));
        userAccountModel.setRoles(new HashSet<>(List.of(roleModel)));

        userAccountRepository.save(userAccountModel);
    }

    protected String generateToken() {
        final UserAccountModel userAccountModel = userAccountRepository.findByUsername(USERNAME).orElseThrow();
        final UserDetails userDetails = new User(userAccountModel.getUsername(),
                userAccountModel.getPassword(),
                userAccountModel.getRoles().stream()
                        .map(roleModel -> new SimpleGrantedAuthority("ROLE_" + roleModel.getName()))
                        .collect(Collectors.toList()));

        return jwtService.generateToken(userDetails);
    }
}

