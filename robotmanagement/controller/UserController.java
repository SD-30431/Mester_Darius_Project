package com.example.robotmanagement.controller;

import com.example.robotmanagement.entity.Robot;
import com.example.robotmanagement.entity.Task;
import com.example.robotmanagement.entity.User;
import com.example.robotmanagement.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.example.robotmanagement.dto.UserDashboardData;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "http://localhost:4200") // Allow frontend URL
public class UserController {
    private final AdminService adminService;

    public UserController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("dashboard")
    public ResponseEntity<UserDashboardData> getUserDashboardData() {
        // Extract username from SecurityContextHolder
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("Fetching dashboard for user: " + username);

        // Fetch user from AdminService
        User user = adminService.getUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        // Fetch robots and tasks related to this user
        List<Robot> userRobots = adminService.getAllRobots().stream()
                .filter(robot -> robot.getOwner() != null && robot.getOwner().getId().equals(user.getId()))
                .toList();

        List<Task> userTasks = adminService.getAllTasks().stream()
                .filter(task -> task.getRobot() != null && task.getRobot().getOwner() != null
                        && task.getRobot().getOwner().getId().equals(user.getId()))
                .toList();

        // Construct and return dashboard data
        UserDashboardData dashboardData = new UserDashboardData();
        dashboardData.setUsername(user.getUsername());
        dashboardData.setRobots(userRobots.stream().map(Robot::getName).toList());
        dashboardData.setTasks(userTasks.stream().map(Task::getName).toList());

        System.out.println("Dashboard Data: " + dashboardData);

        return ResponseEntity.ok(dashboardData);
    }


//    // In UserController
//    @GetMapping("/robots")
//    public ResponseEntity<List<Robot>> getUserRobots() {
//        String username = SecurityContextHolder.getContext().getAuthentication().getName();
//        User user = adminService.getUserByUsername(username)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        List<Robot> userRobots = adminService.getAllRobots().stream()
//                .filter(robot -> robot.getOwner() != null && robot.getOwner().getId().equals(user.getId()))
//                .toList();
//
//        return ResponseEntity.ok(userRobots);
//    }
//
//    @GetMapping("/tasks")
//    public ResponseEntity<List<Task>> getUserTasks() {
//        String username = SecurityContextHolder.getContext().getAuthentication().getName();
//        User user = adminService.getUserByUsername(username)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        List<Task> userTasks = adminService.getAllTasks().stream()
//                .filter(task -> task.getRobot() != null && task.getRobot().getOwner().getId().equals(user.getId()))
//                .toList();
//
//        return ResponseEntity.ok(userTasks);
//    }

    @GetMapping
    public List<User> getAllUsers() {
        return adminService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getUserById(id));
    }

    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        if (user.getId() == 0) {
            // Handle invalid ID for new user, perhaps set the ID to null
            user.setId(null);  // Ensure the ID is null for a new user
        }
        return ResponseEntity.ok(adminService.createUser(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @Valid @RequestBody User user) {
        return ResponseEntity.ok(adminService.updateUser(id, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
