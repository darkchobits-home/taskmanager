package com.sebastien.taskmanager.service;

import com.sebastien.taskmanager.converter.useraccount.UserAccountEntityToModelConverter;
import com.sebastien.taskmanager.converter.useraccount.UserAccountModelToEntityConverter;
import com.sebastien.taskmanager.entity.useraccount.UserAccount;
import com.sebastien.taskmanager.exceptions.RoleException;
import com.sebastien.taskmanager.exceptions.RoleExceptionCode;
import com.sebastien.taskmanager.exceptions.UserAccountException;
import com.sebastien.taskmanager.exceptions.UserAccountExceptionCode;
import com.sebastien.taskmanager.model.RoleModel;
import com.sebastien.taskmanager.model.UserAccountModel;
import com.sebastien.taskmanager.repository.RoleRepository;
import com.sebastien.taskmanager.repository.UserAccountRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserAccountService {

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    @SuppressWarnings("unused")
    private UserAccountModelToEntityConverter userAccountModelToEntityConverter;

    @Autowired
    @SuppressWarnings("unused")
    private UserAccountEntityToModelConverter userAccountEntityToModelConverter;

    @Autowired
    private RoleRepository roleRepository;

    /**
     * Create a userAccount in database.
     *
     * @param userAccount UserAccount to create.
     * @return The id of the new UserAccount.
     */
    public Optional<Long> createUserAccount(final UserAccount userAccount) {
        // Check if user already exists
        Optional<UserAccountModel> userAccountModelOptional = userAccountRepository.findByUsername(userAccount.getUsername());
        if (userAccountModelOptional.isPresent()) {
            final UserAccountException userAccountException = new UserAccountException(UserAccountExceptionCode.USERNAME_ALREADY_EXISTS);
            userAccountException.getDetails().put("Username", userAccount.getUsername());

            throw userAccountException;
        }

        // Check if roles are defined
        if (CollectionUtils.isEmpty(userAccount.getRoles())) {
            final UserAccountException userAccountException = new UserAccountException(UserAccountExceptionCode.NO_ROLE_DEFINED);
            userAccountException.getDetails().put("Username", userAccount.getUsername());

            throw userAccountException;
        }

        final UserAccountModel userAccountModelToSave =  userAccountEntityToModelConverter.convert(userAccount, UserAccountModel.class);

        // Get existing role in database
        final Set<RoleModel> roleModelSet = userAccount.getRoles().stream()
                .map(role -> roleRepository.findByName(role.getName()).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        userAccountModelToSave.setRoles(roleModelSet);

        return save(userAccountModelToSave);
    }

    /**
     * Get all user account in database.
     *
     * @return A set of all the user account in database.
     */
    public Set<UserAccount> getAll() {
        final List<UserAccountModel> allUserAccountModels = userAccountRepository.findAll();

        return allUserAccountModels.stream()
                .map(currentUserAccountModel -> this.userAccountModelToEntityConverter.convert(currentUserAccountModel, UserAccount.class))
                .collect(Collectors.toSet());
    }

    /**
     * Get a user account with the id in parameter.
     *
     * @param id The id of the user account.
     * @return The user account found, or an Optional.empty().
     */
    public Optional<UserAccount> getById(@NotNull Long id) {
        final Optional<UserAccountModel> userAccountModelOptional = userAccountRepository.findById(id);

        return userAccountModelOptional.map(userAccountModel -> userAccountModelToEntityConverter.convert(userAccountModel, UserAccount.class));
    }

    /**
     * Get a user account with the id in parameter.
     *
     * @param username The username of the user account.
     * @return The user account found, or an Optional.empty().
     */
    public Optional<UserAccount> getByUsername(@NotNull String username) {
        final Optional<UserAccountModel> userAccountModelOptional = userAccountRepository.findByUsername(username);

        return userAccountModelOptional.map(userAccountModel -> userAccountModelToEntityConverter.convert(userAccountModel, UserAccount.class));
    }

    /**
     * Update a user account.
     *
     * @param userAccount The user account with new values.
     * @return The id of the user account updated.
     */
    public Optional<Long> updateUserAccount(UserAccount userAccount) {
        UserAccountModel userAccountModel = userAccountRepository.findById(userAccount.getId()).orElseThrow(() -> {
            final UserAccountException userAccountException = new UserAccountException(UserAccountExceptionCode.USER_ACCOUNT_NOT_FOUND);
            userAccountException.getDetails().put("Id", String.valueOf(userAccount.getId()));
            userAccountException.getDetails().put("Username", userAccount.getUsername());

            return userAccountException;
        });

        final UserAccountModel userAccountModelToSave =  userAccountEntityToModelConverter.convert(userAccount, UserAccountModel.class);
        userAccountModelToSave.setId(userAccountModel.getId());
        Set<RoleModel> roleModelSet = userAccountModelToSave.getRoles().stream()
                .map(roleModel -> roleRepository.findByName(roleModel.getName()).orElseThrow( () -> {

                        final RoleException roleException = new RoleException(RoleExceptionCode.ROLE_NOT_FOUND);
                        roleException.getDetails().put("Name", roleModel.getName().name());

                        return roleException;
                    }))
                .collect(Collectors.toSet());

        userAccountModelToSave.setRoles(roleModelSet);

        return save(userAccountModelToSave);
    }

    /**
     * Delete the user account with a specific id.
     *
     * @param userAccountId The id of the user account.
     */
    public void deleteUserAccount(Long userAccountId) {
        Optional<UserAccountModel> userAccountModelOptional = userAccountRepository.findById(userAccountId);

        if (userAccountModelOptional.isEmpty()) {
            final UserAccountException userAccountException = new UserAccountException(UserAccountExceptionCode.USER_ACCOUNT_NOT_FOUND);
            userAccountException.getDetails().put("Id", String.valueOf(userAccountId));

            throw userAccountException;
        }

        userAccountRepository.delete(userAccountModelOptional.get());
    }

    /**
     * Save the user account in database.
     *
     * @param userAccountModel The user account.
     * @return The id of the user account saved.
     */
    public Optional<Long> save(final UserAccountModel userAccountModel) {
        try {
            final UserAccountModel userAccountModelSaved = userAccountRepository.save(userAccountModel);

            return Optional.of(userAccountModelSaved.getId());
        } catch (RuntimeException e) {
            final UserAccountException userAccountException = new UserAccountException(UserAccountExceptionCode.UNKNOWN_EXCEPTION);
            userAccountException.getDetails().put("Cause", e.getCause().toString());
            userAccountException.getDetails().put("UserAccount Model", userAccountModel.toString());

            throw userAccountException;
        }
    }
}
