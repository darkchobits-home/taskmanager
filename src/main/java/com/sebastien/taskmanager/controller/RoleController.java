package com.sebastien.taskmanager.controller;

import com.sebastien.taskmanager.converter.role.RoleDtoToEntityConverter;
import com.sebastien.taskmanager.converter.role.RoleEntityToDtoConverter;
import com.sebastien.taskmanager.dto.role.RoleCreationDTO;
import com.sebastien.taskmanager.dto.role.RoleDTO;
import com.sebastien.taskmanager.dto.role.RoleUpdateDTO;
import com.sebastien.taskmanager.entity.role.Role;
import com.sebastien.taskmanager.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RequestMapping("/api/roles")
@RestController
@SuppressWarnings("unused")
public class RoleController {

    private final RoleService roleService;

    @Autowired
    private RoleDtoToEntityConverter roleDtoToEntityConverter;

    @Autowired
    private RoleEntityToDtoConverter roleEntityToDtoConverter;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }


    @Operation(summary = "Create a role", description = "Return the id of the role created.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully created."),
            @ApiResponse(responseCode = "400", description = "Error bad request, see details in logs."),
            @ApiResponse(responseCode = "409", description = "Error conflict, see details in logs."),
            @ApiResponse(responseCode = "500", description = "Unknown error, see details in logs.")
    })
    @PostMapping
    @Secured("ROLE_ADMIN")
    public ResponseEntity<Long> createRole(@RequestBody RoleCreationDTO roleCreationDTO) {
        final Role role = roleDtoToEntityConverter.convertCreationDtoToEntity(roleCreationDTO);
        final Optional<Long> userAccountId = roleService.createRole(role);

        return userAccountId.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }

    @Operation(summary = "Get all roles", description = "Return the list of roles.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully created."),
            @ApiResponse(responseCode = "500", description = "Unknown error, see details in logs.")
    })
    @GetMapping
    @Secured("ROLE_ADMIN")
    public Set<RoleDTO> getAll() {
        final Set<Role> allRoles = roleService.getAll();

        return allRoles.stream()
                .map(role -> roleEntityToDtoConverter.convert(role, RoleDTO.class))
                .collect(Collectors.toSet());
    }

    @Operation(summary = "Get a role by id", description = "Return the role with the id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully get."),
            @ApiResponse(responseCode = "500", description = "Unknown error, see details in logs.")
    })
    @GetMapping("/{id}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<RoleDTO> getById(@PathVariable @NotNull Long id) {
        Optional<Role> roleOptional = roleService.getById(id);

        return roleOptional.map(role -> ResponseEntity.ok(roleEntityToDtoConverter.convert(role, RoleDTO.class)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Update a role", description = "Return the id of the role updated.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated."),
            @ApiResponse(responseCode = "500", description = "Unknown error, see details in logs.")
    })
    @PutMapping("/update")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<Long> updateRole(@RequestBody RoleUpdateDTO roleUpdateDTO) {
        final Role role = roleDtoToEntityConverter.convertUpdateDtoToEntity(roleUpdateDTO);
        final Optional<Long> roleIdOptional = roleService.updateRole(role);

        return roleIdOptional.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());

    }

    @Operation(summary = "Delete a role", description = "Return the id of the role deleted.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted."),
            @ApiResponse(responseCode = "500", description = "Unknown error, see details in logs.")
    })
    @DeleteMapping("/delete/{roleId}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<Long> deleteRole(@PathVariable @NotNull Long roleId) {
        roleService.deleteRole(roleId);

        return ResponseEntity.ok(roleId);
    }
}
