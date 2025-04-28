package com.example.robotmanagement.ui;

import com.example.robotmanagement.service.AdminService;
import com.example.robotmanagement.service.PasteUrlService;
import com.example.robotmanagement.service.RobotService;
import com.example.robotmanagement.service.TaskService;

import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends JFrame {

    public AdminDashboard(AdminService adminService, RobotService robotService, TaskService taskService, PasteUrlService pasteUrlService, String currentUsername, Long userId) {

        setTitle("Admin Dashboard");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel label = new JLabel("Welcome " + currentUsername + " - Admin Dashboard", JLabel.CENTER);
        add(label, BorderLayout.NORTH);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1, 10, 10));

        JButton manageUsersButton = new JButton("Manage Users");
        JButton manageRobotsButton = new JButton("Manage Robots");
        JButton manageTasksButton = new JButton("Manage Tasks");

        manageUsersButton.addActionListener(_ -> new ManageUsersUI(adminService).setVisible(true));
        manageRobotsButton.addActionListener(_ -> new ManageRobotsUI(robotService, "ADMIN",currentUsername, userId).setVisible(true));  // Pass currentUsername to ManageRobotsUI
        manageTasksButton.addActionListener(_ -> new ManageTasksUI(taskService, robotService, pasteUrlService ,"ADMIN", userId).setVisible(true));

        panel.add(manageUsersButton);
        panel.add(manageRobotsButton);
        panel.add(manageTasksButton);

        add(panel, BorderLayout.CENTER);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
