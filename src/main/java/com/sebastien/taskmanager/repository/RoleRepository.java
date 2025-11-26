package com.sebastien.taskmanager.repository;

import com.sebastien.taskmanager.enums.RoleEnum;
import com.sebastien.taskmanager.model.RoleModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleModel, Long> {
    Optional<RoleModel> findByName(RoleEnum name);
}
