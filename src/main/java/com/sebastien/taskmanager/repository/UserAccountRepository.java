package com.sebastien.taskmanager.repository;

import com.sebastien.taskmanager.model.UserAccountModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UserAccountRepository extends JpaRepository<UserAccountModel, Long> {

    Optional<UserAccountModel> findByUsername(String username);

    Optional<UserAccountModel> findByRefreshToken(String refreshToken);
}
