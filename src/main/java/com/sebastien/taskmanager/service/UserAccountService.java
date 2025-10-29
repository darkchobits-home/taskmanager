package com.sebastien.taskmanager.service;

import com.sebastien.taskmanager.converter.useraccount.UserAccountEntityToDtoConverter;
import com.sebastien.taskmanager.converter.useraccount.UserAccountEntityToModelConverter;
import com.sebastien.taskmanager.converter.useraccount.UserAccountModelToEntityConverter;
import com.sebastien.taskmanager.entity.useraccount.UserAccount;
import com.sebastien.taskmanager.exceptions.UserAccountException;
import com.sebastien.taskmanager.exceptions.UserAccountExceptionCode;
import com.sebastien.taskmanager.model.UserAccountModel;
import com.sebastien.taskmanager.repository.UserAccountRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserAccountService {

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private UserAccountEntityToDtoConverter userAccountEntityToDtoConverter;

    @Autowired
    private UserAccountModelToEntityConverter userAccountModelToEntityConverter;

    @Autowired
    private UserAccountEntityToModelConverter userAccountEntityToModelConverter;


    /**
     * Create a userAccount in database.
     *
     * @param userAccount UserAccount to create.
     * @return Id of the new UserAccount.
     */
    public Optional<Long> createUserAccount(final UserAccount userAccount) {
        // Check if roles are defined
        if (CollectionUtils.isEmpty(userAccount.getRoles())) {
            final UserAccountException userAccountException = new UserAccountException(UserAccountExceptionCode.NO_ROLE_DEFINED);
            userAccountException.getDetails().put("Username", userAccount.getUsername());

            throw userAccountException;
        }

        // Check if user already exists
        Optional<UserAccountModel> userAccountModelOptional = userAccountRepository.findByUsername(userAccount.getUsername());
        if (userAccountModelOptional.isPresent()) {
            final UserAccountException userAccountException = new UserAccountException(UserAccountExceptionCode.USERNAME_ALREADY_EXISTS);
            userAccountException.getDetails().put("Username", userAccount.getUsername());

            throw userAccountException;
        }

        final UserAccountModel userAccountModelToSave =  userAccountEntityToModelConverter.convert(userAccount, UserAccountModel.class);

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
     * Update a user account.
     *
     * @param userAccount The user account with new values.
     * @return The id of the user account updated.
     */
    public Optional<Long> updateUserAccount(UserAccount userAccount) {
        Optional<UserAccountModel> userAccountModelOptional = userAccountRepository.findById(userAccount.getId());

        if (userAccountModelOptional.isEmpty()) {
            final UserAccountException userAccountException = new UserAccountException(UserAccountExceptionCode.USER_ACCOUNT_ID_DOES_NOT_EXIST);
            userAccountException.getDetails().put("Id", String.valueOf(userAccount.getId()));
            userAccountException.getDetails().put("Username", userAccount.getUsername());

            throw userAccountException;
        }

        final UserAccountModel userAccountModelToSave =  userAccountEntityToModelConverter.convert(userAccount, UserAccountModel.class);
        userAccountModelToSave.setId(userAccountModelOptional.get().getId());

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
            final UserAccountException userAccountException = new UserAccountException(UserAccountExceptionCode.USER_ACCOUNT_ID_DOES_NOT_EXIST);
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
        final UserAccountModel userAccountModelSaved = userAccountRepository.save(userAccountModel);

        return Optional.of(userAccountModelSaved.getId());
    }
}
