package com.example.robotmanagement.controller;

import com.example.robotmanagement.entity.Task;
import com.example.robotmanagement.util.PastebinUploader;
import com.example.robotmanagement.service.AdminService;
import com.example.robotmanagement.service.RobotService;
import com.example.robotmanagement.service.TaskService;
import com.example.robotmanagement.util.OpenAIChat;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.swing.*;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;

import java.io.IOException;
import java.util.List;

import static com.example.robotmanagement.util.MicrobitDownloadAutomation.deployCodeToMicrobit;
import static com.example.robotmanagement.util.MicrobitDownloadAutomation.openCode;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "http://localhost:4200") // Allow frontend URL
public class TaskController {
    private final AdminService adminService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private RobotService robotService;

    public TaskController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/open/{id}")
    public ResponseEntity<String> openTaskCode(@PathVariable Long id) {
        // Call your method to open the file or perform the action
        openCode(taskService.getTaskById(id).getName()); // This could return path or message
        return ResponseEntity.ok().build();
    }

    @GetMapping("")
    public List<Task> getAllTasks() {
        return adminService.getAllTasks();
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<List<Task>> getTaskByUserId(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getTaskByUserId(id));
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/simulate/{id}")
    public ResponseEntity<List<String>> simulateCode(@PathVariable Long id) {
        List<String> steps = taskService.simulateCode(PastebinUploader.getAICode(taskService.getTaskById(id).getUrl())); // whatever logic you use
        return ResponseEntity.ok(steps);
    }


    @PostMapping("/create")
    public ResponseEntity<Map<String, String>> createTask(@Valid @RequestBody Task task) {
        Map<String, String> response = new HashMap<>();
        try {
            // 1. Validate input
            if (task.getDescription() == null || task.getRobot() == null || task.getRobot().getId() == null) {
                response.put("message", "Description and robot ID must be provided.");
                return ResponseEntity.badRequest().body(response);
            }

            // 2. Generate Microbit Code via OpenAI
            String generatedCode = OpenAIChat.generateMicrobitCode(task.getDescription());
            if (generatedCode == null || generatedCode.isEmpty()) {
                response.put("message", "Failed to generate code.");
                return ResponseEntity.status(500).body(response);
            }

            // 3. Get the name from first line
            String[] lines = generatedCode.split("\n");
            String name = lines.length > 0 ? lines[0].replace("//", "").trim() : "Unnamed Task";

            // 4. Inject radio group code (based on user ID, assume it's in task.getUser().getId())
            Long robotId = task.getRobot().getId();
            Long userId = robotService.getUserIdByRobotId(robotId);
            if (userId == null) {
                userId = 0L;
            }
            String insertCode = "radio.setGroup(" + (userId + 10) + ")";
            StringBuilder modifiedCode = new StringBuilder();
            modifiedCode.append(lines[0]).append("\n")
                    .append(insertCode).append("\n")
                    .append(String.join("\n", Arrays.copyOfRange(lines, 1, lines.length)));

            // 5. Deploy to microbit (you must implement this logic in your service)
            deployCodeToMicrobit(modifiedCode.toString(), name);
            String pasteUrl = PastebinUploader.uploadCode(String.valueOf(modifiedCode),name);
            // 6. Save Task
            task.setName(name);
            task.setUrl(pasteUrl);
            //task.setCode(modifiedCode.toString()); // Optional: store the code if you want
            System.out.println(modifiedCode);
            taskService.createTask(task);

            // 7. Response
            response.put("message", "Task created and code generated + deployed successfully.");
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            response.put("message", "Error generating code: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }



    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @Valid @RequestBody Task task) {
        return ResponseEntity.ok(adminService.updateTask(id, task));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        adminService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
