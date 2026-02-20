package com.sebastien.taskmanager.service;

import com.sebastien.taskmanager.exceptions.UserAccountException;
import com.sebastien.taskmanager.exceptions.UserAccountExceptionCode;
import com.sebastien.taskmanager.model.UserAccountModel;
import com.sebastien.taskmanager.repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    private UserAccountRepository userAccountRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        final Optional<UserAccountModel> userAccountModelOptional = userAccountRepository.findByUsernameWithRoles(username);

        if (userAccountModelOptional.isEmpty()) {
            final UserAccountException userAccountException = new UserAccountException(UserAccountExceptionCode.USER_ACCOUNT_NOT_FOUND);
            userAccountException.getDetails().put("Username", username);

            throw userAccountException;
        }

        UserAccountModel userAccountModel = userAccountModelOptional.get();

        return new User(userAccountModel.getUsername(),
                userAccountModel.getPassword(),
                userAccountModel.getRoles().stream()
                        .map(roleModel -> new SimpleGrantedAuthority("ROLE_" + roleModel.getName().name()))
                        .collect(Collectors.toList()));
    }
}
