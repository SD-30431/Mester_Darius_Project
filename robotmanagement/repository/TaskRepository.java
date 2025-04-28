package com.example.robotmanagement.repository;

import com.example.robotmanagement.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByRobotIdIn(List<Long> robotIds);
}
