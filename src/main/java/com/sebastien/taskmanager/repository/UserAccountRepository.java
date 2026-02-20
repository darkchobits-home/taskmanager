package com.sebastien.taskmanager.repository;

import com.sebastien.taskmanager.model.UserAccountModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;


public interface UserAccountRepository extends JpaRepository<UserAccountModel, Long> {

    Optional<UserAccountModel> findByUsername(String username);

    @Query("""
            Select u From UserAccount u
            Join Fetch u.roles
            Where u.username = :username 
            """)
    Optional<UserAccountModel> findByUsernameWithRoles(String username);

    Optional<UserAccountModel> findByRefreshToken(String refreshToken);
}
