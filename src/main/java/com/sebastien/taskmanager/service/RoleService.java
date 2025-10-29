package com.sebastien.taskmanager.service;

import com.sebastien.taskmanager.converter.role.RoleEntityToModelConverter;
import com.sebastien.taskmanager.converter.role.RoleModelToEntityConverter;
import com.sebastien.taskmanager.entity.role.Role;
import com.sebastien.taskmanager.entity.useraccount.UserAccount;
import com.sebastien.taskmanager.exceptions.RoleException;
import com.sebastien.taskmanager.exceptions.RoleExceptionCode;
import com.sebastien.taskmanager.exceptions.UserAccountException;
import com.sebastien.taskmanager.exceptions.UserAccountExceptionCode;
import com.sebastien.taskmanager.model.RoleModel;
import com.sebastien.taskmanager.model.UserAccountModel;
import com.sebastien.taskmanager.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RoleEntityToModelConverter roleEntityToModelConverter;

    @Autowired
    private RoleModelToEntityConverter roleModelToEntityConverter;

    /**
     * Create a role in database.
     *
     * @param role Role to create.
     * @return The id of the new Role.
     */
    public Optional<Long> createRole(Role role) {
        // Check if role already exists
        final Optional<RoleModel> roleModelOptional = roleRepository.findByName(role.getName());
        if (roleModelOptional.isPresent()) {
            final RoleException roleException = new RoleException(RoleExceptionCode.ROLE_NAME_ALREADY_EXISTS);
            roleException.getDetails().put("Name", role.getName());

            throw roleException;
        }

        final RoleModel roleModelToSave = roleEntityToModelConverter.convert(role, RoleModel.class);

        return save(roleModelToSave);
    }

    /**
     * Get all roles in database.
     *
     * @return A set of all the roles in database.
     */
    public Set<Role> getAll() {
        final List<RoleModel> allRoles = roleRepository.findAll();

        return allRoles.stream()
                .map(roleModel ->  roleModelToEntityConverter.convert(roleModel, Role.class))
                .collect(Collectors.toSet());
    }

    /**
     * Get a role with the id in parameter.
     *
     * @param id The id of the role.
     * @return The role found, or an Optional.empty().
     */
    public Optional<Role> getById(Long id) {
        final Optional<RoleModel> roleModelOptional = roleRepository.findById(id);

        return roleModelOptional.map(roleModel -> roleModelToEntityConverter.convert(roleModel, Role.class));
    }

    /**
     * Update a role.
     *
     * @param role The role with new values.
     * @return The id of the role updated.
     */
    public Optional<Long> updateRole(Role role) {
        Optional<RoleModel> RoleModelOptional = roleRepository.findById(role.getId());

        if (RoleModelOptional.isEmpty()) {
            final RoleException RoleException = new RoleException(RoleExceptionCode.ROLE_ID_DOES_NOT_EXIST);
            RoleException.getDetails().put("Id", String.valueOf(role.getId()));
            RoleException.getDetails().put("name", role.getName());

            throw RoleException;
        }

        final RoleModel roleModelToSave =  roleEntityToModelConverter.convert(role, RoleModel.class);
        roleModelToSave.setId(RoleModelOptional.get().getId());

        return save(roleModelToSave);
    }

    /**
     * Delete the role with a specific id.
     *
     * @param roleId The id of the role.
     */
    public void deleteRole(Long roleId) {
        Optional<RoleModel> roleModelOptional = roleRepository.findById(roleId);

        if (roleModelOptional.isEmpty()) {
            final RoleException roleException = new RoleException(RoleExceptionCode.ROLE_ID_DOES_NOT_EXIST);
            roleException.getDetails().put("Id", String.valueOf(roleId));

            throw roleException;
        }

        roleRepository.delete(roleModelOptional.get());
    }

    /**
     * Save the role in database.
     *
     * @param roleModel The role.
     * @return The id of the role saved.
     */
    public Optional<Long> save(final RoleModel roleModel) {
        final RoleModel roleModelSaved = roleRepository.save(roleModel);

        return Optional.of(roleModelSaved.getId());
    }
}
