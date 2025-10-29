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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = TaskmanagerApplication.class)
public class UserAccountServiceTest {

    @Autowired
    private RoleService roleService;

    @MockitoBean
    private RoleRepository roleRepository;

    @Test
    public void testGetAllRoles() {
        // When
        final RoleModel roleModel1Provided = new RoleModel();
        roleModel1Provided.setId(1L);
        roleModel1Provided.setName("USER");

        final RoleModel roleModel2Provided = new RoleModel();
        roleModel2Provided.setId(2L);
        roleModel2Provided.setName("ADMIN");

        final List<RoleModel> roleModelListProvided = Arrays.asList(roleModel1Provided, roleModel2Provided);

        Mockito.when(roleRepository.findAll()).thenReturn(roleModelListProvided);

        // Then
        Set<Role> roleSetResult =  roleService.getAll();

        // Asserts
        final Role role1Expected = new Role();
        role1Expected.setId(1L);
        role1Expected.setName("USER");

        final Role role2Expected = new Role();
        role2Expected.setId(2L);
        role2Expected.setName("ADMIN");

        final Set<Role> roleSetExpected = new HashSet<>(Arrays.asList(role1Expected, role2Expected));

        assertThat(roleSetResult).hasSize(2);
        assertThat(roleSetResult).containsAll(roleSetExpected);

    }

    @Test
    public void testGetById() {
        // When
        final RoleModel roleModel1Provided = new RoleModel();
        roleModel1Provided.setId(1L);
        roleModel1Provided.setName("USER");

        Mockito.when(roleRepository.findById(1L)).thenReturn(Optional.of(roleModel1Provided));

        // Then
        Optional<Role> roleResult =  roleService.getById(1L);

        // Asserts
        final Role role1Expected = new Role();
        role1Expected.setId(1L);
        role1Expected.setName("USER");

        assertThat(roleResult).isNotEmpty();
        assertEquals(roleResult.get(), role1Expected);

    }

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
        RoleModel roleModelProvided = new RoleModel();
        roleModelProvided.setName("USER");

        Mockito.when(roleRepository.findByName(roleModelProvided.getName())).thenReturn(Optional.of(roleModelProvided));

        Role roleProvided = new Role();
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

        Role roleProvided = new Role();
        roleProvided.setId(1L);
        roleProvided.setName("USER");

        // Then
        final RoleException roleExceptionResult = assertThrows(RoleException.class, () -> roleService.updateRole(roleProvided));

        // Assert
        assertThat(roleExceptionResult).isNotNull();
        assertThat(roleExceptionResult.getRoleExceptionCode()).isSameAs(RoleExceptionCode.ROLE_ID_DOES_NOT_EXIST);
    }
}
