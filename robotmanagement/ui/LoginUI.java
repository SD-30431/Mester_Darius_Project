package com.example.robotmanagement.ui;

import com.example.robotmanagement.RobotmanagementApplication;
import com.example.robotmanagement.entity.User;
import com.example.robotmanagement.service.*;

import javax.swing.*;
import java.awt.*;

public class LoginUI extends JFrame {
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final AuthService authService;

    public LoginUI(AuthService authService) {
        this.authService = authService;

        setTitle("Robot Management System - Login");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 2, 10, 10));

        add(new JLabel("Username:"));
        usernameField = new JTextField();
        add(usernameField);

        add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        add(passwordField);

        JButton loginButton = new JButton("Login");
        add(loginButton);

        loginButton.addActionListener(_ -> authenticateUser());

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void authenticateUser() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        User user = authService.authenticate(username, password);
        if (user == null) {
            JOptionPane.showMessageDialog(this, "Invalid Credentials", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String role = user.getRole().name();
        String name = user.getUsername();
        Long user_id = user.getId();

        RobotService robotService = RobotmanagementApplication.getRobotService();
        TaskService taskService = RobotmanagementApplication.getTaskService();
        PasteUrlService pasteUrlService = RobotmanagementApplication.getPasteUrlService();
        // Close the login screen
        dispose();

        // Show the appropriate dashboard based on role
        if (role.equals("ADMIN")) {
            AdminService adminService = RobotmanagementApplication.getAdminService();
            AdminDashboard adminDashboard = new AdminDashboard(adminService, robotService, taskService, pasteUrlService, name, user_id);
            adminDashboard.setVisible(true);  // Make the admin dashboard visible
        } else {
            UserDashboard userDashboard = new UserDashboard(robotService, taskService, pasteUrlService, name, user_id);
            userDashboard.setVisible(true);  // Make the user dashboard visible
        }
    }
}
