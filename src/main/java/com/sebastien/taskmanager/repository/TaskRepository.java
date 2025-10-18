package com.sebastien.taskmanager.repository;

import com.sebastien.taskmanager.model.TaskModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<TaskModel, Long> {
}
