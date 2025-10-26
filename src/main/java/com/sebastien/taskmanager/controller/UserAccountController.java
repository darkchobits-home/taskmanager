package com.sebastien.taskmanager.controller;

import com.sebastien.taskmanager.converter.useraccount.UserAccountDtoToEntityConverter;
import com.sebastien.taskmanager.converter.useraccount.UserAccountEntityToDtoConverter;
import com.sebastien.taskmanager.dto.useraccount.UserAccountCreationDTO;
import com.sebastien.taskmanager.dto.useraccount.UserAccountDTO;
import com.sebastien.taskmanager.entity.useraccount.UserAccount;
import com.sebastien.taskmanager.service.UserAccountService;
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

    @Autowired
    private UserAccountDtoToEntityConverter userAccountDtoToEntityConverter;

    @Autowired
    private UserAccountEntityToDtoConverter userAccountEntityToDtoConverter;

    public UserAccountController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @GetMapping
    public Set<UserAccountDTO> getAll() {
        final Set<UserAccount> allUserAccount = userAccountService.getAll();

        return allUserAccount.stream().map(userAccount -> userAccountEntityToDtoConverter.convert(userAccount, UserAccountDTO.class)).collect(Collectors.toSet());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserAccountDTO> getById(@PathVariable @NotNull Long id) {
        final Optional<UserAccount> userAccountOptional = userAccountService.getById(id);

        return userAccountOptional.map(userAccount -> ResponseEntity.ok(userAccountEntityToDtoConverter.convert(userAccount, UserAccountDTO.class))).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Long> createUser(@RequestBody UserAccountCreationDTO userAccountCreationDTO) {

        final UserAccount userAccount = userAccountDtoToEntityConverter.convertCreationDtoToEntity(userAccountCreationDTO);
        final Optional<Long> userAccountId = userAccountService.createUserAccount(userAccount);

        return userAccountId.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());

    }
}
