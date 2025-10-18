package com.sebastien.taskmanager.repository;

import com.sebastien.taskmanager.model.RoleModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<RoleModel, Long> {
}
