package com.example.robotmanagement.service;

import com.example.robotmanagement.entity.Robot;
import com.example.robotmanagement.entity.Task;
import com.example.robotmanagement.entity.User;
import com.example.robotmanagement.repository.RobotRepository;
import com.example.robotmanagement.repository.TaskRepository;
import com.example.robotmanagement.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AdminService {
    private final UserRepository userRepository;
    private final RobotRepository robotRepository;
    private final TaskRepository taskRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AdminService(UserRepository userRepository, RobotRepository robotRepository, TaskRepository taskRepository) {
        this.userRepository = userRepository;
        this.robotRepository = robotRepository;
        this.taskRepository = taskRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    // USER CRUD
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Encrypt password
        user.setCreated_at(LocalDateTime.now()); // Set timestamp
        return userRepository.save(user);
    }

    public User updateUser(Long id, User user) {
        User existingUser = getUserById(id);
        existingUser.setUsername(user.getUsername());

        // Only update the password if it's not empty
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        existingUser.setRole(user.getRole());
        existingUser.setCreated_at(LocalDateTime.now()); // Update timestamp
        return userRepository.save(existingUser);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    // ROBOT CRUD
    public List<Robot> getAllRobots() {
        return robotRepository.findAll();
    }

    public List<Robot> getRobotsByOwnerId(Long id) {
        List<Robot> robots = robotRepository.findByOwnerId(id);
        if (robots.isEmpty()) {
            throw new RuntimeException("No robots found for the given owner ID");
        }
        return robots;
    }

    public Robot getRobotById(Long id) {
        return robotRepository.findById(id).orElseThrow(() -> new RuntimeException("Robot not found"));
    }

    public Robot createRobot(Robot robot) {
        return robotRepository.save(robot);
    }

    public Robot updateRobot(Long id, Robot robot) {
        Robot existingRobot = getRobotById(id);
        existingRobot.setName(robot.getName());

        // Only set owner by ID to prevent deep recursion issues
        if (robot.getOwner() != null && robot.getOwner().getId() != null) {
            User owner = userRepository.findById(robot.getOwner().getId())
                    .orElseThrow(() -> new RuntimeException("Owner not found"));
            existingRobot.setOwner(owner);
        }

        return robotRepository.save(existingRobot);
    }

    public void deleteRobot(Long id) {
        robotRepository.deleteById(id);
    }

    // TASK CRUD
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Task getTaskById(Long id) {
        return taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found"));
    }

    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    public List<Task> getTaskByUserId(Long userId) {
        List<Robot> robots = robotRepository.findByOwnerId(userId);
        if (robots.isEmpty()) {
            throw new RuntimeException("No robots found for this user");
        }

        List<Long> robotIds = robots.stream().map(Robot::getId).toList();
        return taskRepository.findByRobotIdIn(robotIds);
    }

    public Task updateTask(Long id, Task task) {
        Task existingTask = getTaskById(id);
        existingTask.setDescription(task.getDescription());
        existingTask.setName(task.getName());
        existingTask.setStatus(task.getStatus());
        existingTask.setRobot(task.getRobot());
        return taskRepository.save(existingTask);
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }
}
