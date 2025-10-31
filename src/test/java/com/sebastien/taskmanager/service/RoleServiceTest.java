package com.sebastien.taskmanager.service;

import com.sebastien.taskmanager.TaskmanagerApplication;
import com.sebastien.taskmanager.entity.role.Role;
import com.sebastien.taskmanager.exceptions.RoleException;
import com.sebastien.taskmanager.exceptions.RoleExceptionCode;
import com.sebastien.taskmanager.model.RoleModel;
import com.sebastien.taskmanager.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = TaskmanagerApplication.class)
@TestPropertySource(locations = "classpath:application-integrationtest.yml")
public class RoleServiceTest {

    @Autowired
    private RoleService roleService;

    @MockitoBean
    private RoleRepository roleRepository;

    @Test
    public void testGetById_IdDoesNotExist() {
        // When
        final RoleException roleException = new RoleException(RoleExceptionCode.ROLE_ID_DOES_NOT_EXIST);

        Mockito.when(roleRepository.findById(1L)).thenThrow(roleException);

        // Then
        final RoleException roleExceptionResult = assertThrows(RoleException.class, () -> roleService.getById(1L));

        // Asserts
        assertThat(roleExceptionResult).isNotNull();
        assertThat(roleExceptionResult.getRoleExceptionCode()).isSameAs(RoleExceptionCode.ROLE_ID_DOES_NOT_EXIST);
    }


    @Test
    public void testCreateRole_NameAlreadyExist() {
        // When
        final RoleModel roleModelProvided = new RoleModel();
        roleModelProvided.setName("USER");

        Mockito.when(roleRepository.findByName(roleModelProvided.getName())).thenReturn(Optional.of(roleModelProvided));

        final Role roleProvided = new Role();
        roleProvided.setName("USER");

        // Then
        final RoleException roleExceptionResult = assertThrows(RoleException.class, () -> roleService.createRole(roleProvided));

        // Assert
        assertThat(roleExceptionResult).isNotNull();
        assertThat(roleExceptionResult.getRoleExceptionCode()).isSameAs(RoleExceptionCode.ROLE_NAME_ALREADY_EXISTS);
    }

    @Test
    public void testUpdateRole_RoleIdDoesNotExist() {
        // When
        Mockito.when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        final Role roleProvided = new Role();
        roleProvided.setId(1L);
        roleProvided.setName("USER");

        // Then
        final RoleException roleExceptionResult = assertThrows(RoleException.class, () -> roleService.updateRole(roleProvided));

        // Assert
        assertThat(roleExceptionResult).isNotNull();
        assertThat(roleExceptionResult.getRoleExceptionCode()).isSameAs(RoleExceptionCode.ROLE_ID_DOES_NOT_EXIST);
    }
}
