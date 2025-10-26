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

    public Set<UserAccount> getAll() {
        final List<UserAccountModel> allUserAccountModels = userAccountRepository.findAll();

        return allUserAccountModels.stream()
                .map(currentUserAccountModel -> this.userAccountModelToEntityConverter.convert(currentUserAccountModel, UserAccount.class))
                .collect(Collectors.toSet());
    }

    public Optional<UserAccount> getById(@NotNull Long id) {
        final Optional<UserAccountModel> userAccountModelOptional = userAccountRepository.findById(id);

        return userAccountModelOptional.map(userAccountModel -> userAccountModelToEntityConverter.convert(userAccountModel, UserAccount.class));
    }

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

    public Optional<Long> save(final UserAccountModel userAccountModel) {
        final UserAccountModel userAccountModelSaved = userAccountRepository.save(userAccountModel);

        return Optional.of(userAccountModelSaved.getId());
    }
}
