package com.sebastien.taskmanager.repository;

import com.sebastien.taskmanager.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<Users, Long> {

}
