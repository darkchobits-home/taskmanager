package com.sebastien.taskmanager.controller;

import com.sebastien.taskmanager.converter.useraccount.UserAccountDtoToEntityConverter;
import com.sebastien.taskmanager.converter.useraccount.UserAccountEntityToDtoConverter;
import com.sebastien.taskmanager.dto.useraccount.UserAccountCreationDTO;
import com.sebastien.taskmanager.dto.useraccount.UserAccountDTO;
import com.sebastien.taskmanager.dto.useraccount.UserAccountUpdateDTO;
import com.sebastien.taskmanager.entity.useraccount.UserAccount;
import com.sebastien.taskmanager.service.UserAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RequestMapping("/api/useraccount")
@RestController
@SuppressWarnings("unused")
public class UserAccountController {

    private final UserAccountService userAccountService;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    private UserAccountDtoToEntityConverter userAccountDtoToEntityConverter;

    @Autowired
    private UserAccountEntityToDtoConverter userAccountEntityToDtoConverter;

    public UserAccountController(UserAccountService userAccountService, PasswordEncoder passwordEncoder) {
        this.userAccountService = userAccountService;
        this.passwordEncoder = passwordEncoder;
    }

    @Operation(summary = "Create a user account", description = "Return the user account id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully created."),
            @ApiResponse(responseCode = "400", description = "Error, bad request."),
            @ApiResponse(responseCode = "500", description = "Unknown error, see details in logs.")
    })
    @PostMapping
    @Secured("ROLE_ADMIN")
    public ResponseEntity<Long> createUser(@Valid @RequestBody UserAccountCreationDTO userAccountCreationDTO) {

        final UserAccount userAccount = userAccountDtoToEntityConverter.convertCreationDtoToEntity(userAccountCreationDTO);
        final String passwordEncoded = passwordEncoder.encode(userAccount.getPassword());
        userAccount.setPassword(passwordEncoded);

        final Optional<Long> userAccountId = userAccountService.createUserAccount(userAccount);

        return userAccountId.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());

    }

    @Operation(summary = "Get all user accounts", description = "Return the list of user accounts.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully get."),
            @ApiResponse(responseCode = "500", description = "Unknown error, see details in logs.")
    })
    @GetMapping
    @Secured("ROLE_ADMIN")
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
    @Secured("ROLE_ADMIN")
    public ResponseEntity<UserAccountDTO> getById(@PathVariable @NotNull Long id) {
        final Optional<UserAccount> userAccountOptional = userAccountService.getById(id);

        return userAccountOptional.map(userAccount -> ResponseEntity.ok(userAccountEntityToDtoConverter.convert(userAccount, UserAccountDTO.class))).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Update a user account", description = "Return the id of the user account updated.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated."),
            @ApiResponse(responseCode = "500", description = "Unknown error, see details in logs.")
    })
    @PutMapping("/update")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<Long> updateUser(@Valid @RequestBody UserAccountUpdateDTO userAccountUpdateDTO) {

        final UserAccount userAccount = userAccountDtoToEntityConverter.convertUpdateDtoToEntity(userAccountUpdateDTO);
        final String passwordEncoded = passwordEncoder.encode(userAccount.getPassword());
        userAccount.setPassword(passwordEncoded);

        final Optional<Long> userAccountId = userAccountService.updateUserAccount(userAccount);

        return userAccountId.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());

    }

    @Operation(summary = "Delete a user account", description = "Return the id of the user account deleted.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted."),
            @ApiResponse(responseCode = "500", description = "Unknown error, see details in logs.")
    })
    @DeleteMapping("/delete/{userAccountId}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<Long> deleteUser(@PathVariable @NotNull Long userAccountId) {
        userAccountService.deleteUserAccount(userAccountId);

        return ResponseEntity.ok(userAccountId);
    }
}
