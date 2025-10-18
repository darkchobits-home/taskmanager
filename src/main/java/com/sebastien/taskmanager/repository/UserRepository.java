package com.sebastien.taskmanager.repository;

import com.sebastien.taskmanager.model.UserAccountModel;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<UserAccountModel, Long> {

}
