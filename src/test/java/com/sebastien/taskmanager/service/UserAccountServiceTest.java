package com.sebastien.taskmanager.service;

import com.sebastien.taskmanager.TaskmanagerApplication;
import com.sebastien.taskmanager.entity.role.Role;
import com.sebastien.taskmanager.entity.useraccount.UserAccount;
import com.sebastien.taskmanager.exceptions.RoleException;
import com.sebastien.taskmanager.exceptions.RoleExceptionCode;
import com.sebastien.taskmanager.exceptions.UserAccountException;
import com.sebastien.taskmanager.exceptions.UserAccountExceptionCode;
import com.sebastien.taskmanager.model.RoleModel;
import com.sebastien.taskmanager.model.UserAccountModel;
import com.sebastien.taskmanager.repository.UserAccountRepository;
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
    private UserAccountService userAccountService;

    @MockitoBean
    private UserAccountRepository userAccountRepository;

    @Test
    public void testGetById_IdDoesNotExist() {
        // When
        final UserAccountException roleException = new UserAccountException(UserAccountExceptionCode.USER_ACCOUNT_ID_DOES_NOT_EXIST);

        Mockito.when(userAccountRepository.findById(1L)).thenThrow(roleException);

        // Then
        final UserAccountException userAccountExceptionResult = assertThrows(UserAccountException.class, () -> userAccountService.getById(1L));

        // Asserts
        assertThat(userAccountExceptionResult).isNotNull();
        assertThat(userAccountExceptionResult.getUserAccountExceptionCode()).isSameAs(UserAccountExceptionCode.USER_ACCOUNT_ID_DOES_NOT_EXIST);
    }


    @Test
    public void testCreateUserAccount_NameAlreadyExist() {
        // When
        final RoleModel roleModel1Provided = new RoleModel();
        roleModel1Provided.setId(1L);
        roleModel1Provided.setName("USER");

        final UserAccountModel userAccountModel1Provided = new UserAccountModel();
        userAccountModel1Provided.setId(1L);
        userAccountModel1Provided.setEmail("email@user1.com");
        userAccountModel1Provided.setUsername("name1");
        userAccountModel1Provided.setPassword("pass1");
        userAccountModel1Provided.setRoles(new HashSet<>(List.of(roleModel1Provided)));

        Mockito.when(userAccountRepository.findByUsername(userAccountModel1Provided.getUsername())).thenReturn(Optional.of(userAccountModel1Provided));

        final Role role1Provided = new Role();
        role1Provided.setId(1L);
        role1Provided.setName("USER");

        final UserAccount userAccount1Provided = new UserAccount();
        userAccount1Provided.setEmail("email@user1.com");
        userAccount1Provided.setUsername("name1");
        userAccount1Provided.setPassword("pass1");
        userAccount1Provided.setRoles(new HashSet<>(List.of(role1Provided)));

        // Then
        final UserAccountException userAccountExceptionResult = assertThrows(UserAccountException.class, () -> userAccountService.createUserAccount(userAccount1Provided));

        // Assert
        assertThat(userAccountExceptionResult).isNotNull();
        assertThat(userAccountExceptionResult.getUserAccountExceptionCode()).isSameAs(UserAccountExceptionCode.USERNAME_ALREADY_EXISTS);
    }

    @Test
    public void testCreateUserAccount_NoRoleDefined() {
        // When
        Mockito.when(userAccountRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.empty());

        final UserAccount userAccount1Provided = new UserAccount();
        userAccount1Provided.setEmail("email@user1.com");
        userAccount1Provided.setUsername("name1");
        userAccount1Provided.setPassword("pass1");
        userAccount1Provided.setRoles(new HashSet<>());

        // Then
        final UserAccountException userAccountExceptionResult = assertThrows(UserAccountException.class, () -> userAccountService.createUserAccount(userAccount1Provided));

        // Assert
        assertThat(userAccountExceptionResult).isNotNull();
        assertThat(userAccountExceptionResult.getUserAccountExceptionCode()).isSameAs(UserAccountExceptionCode.NO_ROLE_DEFINED);
    }

    @Test
    public void testUpdateUserAccount_UserAccountIdDoesNotExist() {
        // When
        Mockito.when(userAccountRepository.findById(1L)).thenReturn(Optional.empty());

        final Role role1Provided = new Role();
        role1Provided.setId(1L);
        role1Provided.setName("USER");

        final UserAccount userAccount1Provided = new UserAccount();
        userAccount1Provided.setId(1L);
        userAccount1Provided.setEmail("email@user1.com");
        userAccount1Provided.setUsername("name1");
        userAccount1Provided.setPassword("pass1");
        userAccount1Provided.setRoles(new HashSet<>(List.of(role1Provided)));

        // Then
        final UserAccountException userAccountExceptionResult = assertThrows(UserAccountException.class, () -> userAccountService.updateUserAccount(userAccount1Provided));

        // Assert
        assertThat(userAccountExceptionResult).isNotNull();
        assertThat(userAccountExceptionResult.getUserAccountExceptionCode()).isSameAs(UserAccountExceptionCode.USER_ACCOUNT_ID_DOES_NOT_EXIST);
    }
}
