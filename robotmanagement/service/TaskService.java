package com.example.robotmanagement.service;

import com.example.robotmanagement.entity.Task;
import com.example.robotmanagement.entity.TaskStatus;
import com.example.robotmanagement.entity.Robot;
import com.example.robotmanagement.repository.TaskRepository;
import com.example.robotmanagement.repository.RobotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final RobotRepository robotRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository, RobotRepository robotRepository) {
        this.taskRepository = taskRepository;
        this.robotRepository = robotRepository;
    }

    public List<String> simulateCode(String code) {
        List<String> steps = new ArrayList<>();
        steps.add("🚀 Simulation started");

        if (code.contains("forward")) steps.add("🚗 Robot moves forward");
        if (code.contains("backward")) steps.add("🔙 Robot moves backward");
        if (code.contains("left")) steps.add("↩️ Robot turns left");
        if (code.contains("right")) steps.add("↪️ Robot turns right");
        if (code.contains("stop")) steps.add("🛑 Robot stops");

        steps.add("✅ Simulation ended");
        return steps;
    }


    public List<Task> getTasksForUserRobots(Long userId) {
        List<Long> userRobotIds = robotRepository.findRobotIdsByOwnerId(userId);
        return taskRepository.findByRobotIdIn(userRobotIds);
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Task getTaskById(Long id) {
        return taskRepository.findById(id).orElse(null);
    }

    public void createTask(Task task) {
        Robot robot = robotRepository.findById(task.getRobot().getId()).orElse(null);
        if (robot == null) {
            throw new IllegalArgumentException("Robot not found with ID: " + task.getRobot().getId());
        }
        taskRepository.save(task);
    }

    public void updateTask(Long id, Task updatedTask) {
        if (taskRepository.existsById(id)) {
            taskRepository.save(updatedTask);
        }
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }
}
