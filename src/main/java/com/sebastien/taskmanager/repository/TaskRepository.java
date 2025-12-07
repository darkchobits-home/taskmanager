package com.sebastien.taskmanager.repository;

import com.sebastien.taskmanager.model.TaskModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<TaskModel, Long> {
    Page<TaskModel> findByTitleContainingIgnoreCase(String title, Pageable pageable);
}
