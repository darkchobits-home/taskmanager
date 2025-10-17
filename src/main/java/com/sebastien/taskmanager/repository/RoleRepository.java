package com.sebastien.taskmanager.repository;

import com.sebastien.taskmanager.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
