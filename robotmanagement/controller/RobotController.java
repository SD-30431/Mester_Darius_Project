package com.example.robotmanagement.controller;

import com.example.robotmanagement.entity.Robot;
import com.example.robotmanagement.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/robots")
@CrossOrigin(origins = "http://localhost:4200") // Allow frontend URL
public class RobotController {
    private final AdminService adminService;

    public RobotController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("")
    public ResponseEntity<List<Robot>> getAllRobots() {
        return ResponseEntity.ok(adminService.getAllRobots());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<List<Robot>> getRobotById(@PathVariable Long id) {
        System.out.println("Get robots by id: " + id);
        return ResponseEntity.ok(adminService.getRobotsByOwnerId(id));
    }

    @PostMapping("/create")
    public ResponseEntity<Robot> createRobot(@Valid @RequestBody Robot robot) {
        return ResponseEntity.ok(adminService.createRobot(robot));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Robot> updateRobot(@PathVariable Long id, @Valid @RequestBody Robot robot) {
        return ResponseEntity.ok(adminService.updateRobot(id, robot));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteRobot(@PathVariable Long id) {
        adminService.deleteRobot(id);
        return ResponseEntity.noContent().build();
    }

}
