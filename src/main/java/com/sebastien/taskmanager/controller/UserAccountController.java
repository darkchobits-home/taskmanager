package com.sebastien.taskmanager.controller;

import com.sebastien.taskmanager.converter.useraccount.UserAccountDtoToEntityConverter;
import com.sebastien.taskmanager.converter.useraccount.UserAccountEntityToDtoConverter;
import com.sebastien.taskmanager.dto.useraccount.UserAccountCreationDTO;
import com.sebastien.taskmanager.dto.useraccount.UserAccountDTO;
import com.sebastien.taskmanager.dto.useraccount.UserAccountUpdateDTO;
import com.sebastien.taskmanager.entity.role.Role;
import com.sebastien.taskmanager.entity.useraccount.UserAccount;
import com.sebastien.taskmanager.service.RoleService;
import com.sebastien.taskmanager.service.UserAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RequestMapping("/api/useraccount")
@RestController
public class UserAccountController {

    private final UserAccountService userAccountService;

    private final RoleService roleService;

    @Autowired
    private UserAccountDtoToEntityConverter userAccountDtoToEntityConverter;

    @Autowired
    private UserAccountEntityToDtoConverter userAccountEntityToDtoConverter;

    public UserAccountController(UserAccountService userAccountService, RoleService roleService) {
        this.userAccountService = userAccountService;
        this.roleService = roleService;
    }

    @Operation(summary = "Create a user account", description = "Return the user account id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully created."),
            @ApiResponse(responseCode = "400", description = "Error, bad request."),
            @ApiResponse(responseCode = "500", description = "Unknown error, see details in logs.")
    })
    @PostMapping
    public ResponseEntity<Long> createUser(@RequestBody UserAccountCreationDTO userAccountCreationDTO) {

        final UserAccount userAccount = userAccountDtoToEntityConverter.convertCreationDtoToEntity(userAccountCreationDTO);

        final Optional<Long> userAccountId = userAccountService.createUserAccount(userAccount);

        return userAccountId.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());

    }

    @Operation(summary = "Get all user accounts", description = "Return the list of user accounts.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully get."),
            @ApiResponse(responseCode = "500", description = "Unknown error, see details in logs.")
    })
    @GetMapping
    public Set<UserAccountDTO> getAll() {
        final Set<UserAccount> allUserAccount = userAccountService.getAll();

        return allUserAccount.stream().map(userAccount -> userAccountEntityToDtoConverter.convert(userAccount, UserAccountDTO.class)).collect(Collectors.toSet());
    }

    @Operation(summary = "Get a user account by id", description = "Return the user account with the id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully get."),
            @ApiResponse(responseCode = "500", description = "Unknown error, see details in logs.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserAccountDTO> getById(@PathVariable @NotNull Long id) {
        final Optional<UserAccount> userAccountOptional = userAccountService.getById(id);

        return userAccountOptional.map(userAccount -> ResponseEntity.ok(userAccountEntityToDtoConverter.convert(userAccount, UserAccountDTO.class))).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Update a user account", description = "Return the id of the user account updated.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated."),
            @ApiResponse(responseCode = "500", description = "Unknown error, see details in logs.")
    })
    @PostMapping("/update")
    public ResponseEntity<Long> updateUser(@RequestBody UserAccountUpdateDTO userAccountUpdateDTO) {

        final UserAccount userAccount = userAccountDtoToEntityConverter.convertUpdateDtoToEntity(userAccountUpdateDTO);
        final Optional<Long> userAccountId = userAccountService.updateUserAccount(userAccount);

        return userAccountId.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());

    }

    @Operation(summary = "Delete a user account", description = "Return the id of the user account deleted.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted."),
            @ApiResponse(responseCode = "500", description = "Unknown error, see details in logs.")
    })
    @PostMapping("/delete/{id}")
    public ResponseEntity<Long> deleteUser(@PathVariable @NotNull Long userAccountId) {
        userAccountService.deleteUserAccount(userAccountId);

        return ResponseEntity.ok(userAccountId);
    }
}
