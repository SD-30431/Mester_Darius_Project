package com.example.robotmanagement.ui;

import com.example.robotmanagement.service.PasteUrlService;
import com.example.robotmanagement.service.RobotService;
import com.example.robotmanagement.service.TaskService;

import javax.swing.*;
import java.awt.*;

public class UserDashboard extends JFrame {

    public UserDashboard(RobotService robotService, TaskService taskService, PasteUrlService pasteUrlService, String currentUsername, Long userId) {

        setTitle(currentUsername + " Dashboard");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel label = new JLabel("User Dashboard - Manage Your Robots & Tasks", JLabel.CENTER);
        add(label, BorderLayout.NORTH);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 1, 10, 10));

        JButton viewRobotsButton = new JButton("Manage My Robots");
        JButton viewTasksButton = new JButton("View My Tasks");

        // Open ManageRobotsUI for the user
        viewRobotsButton.addActionListener(_ -> new ManageRobotsUI(robotService, "USER", currentUsername, userId).setVisible(true));
        viewTasksButton.addActionListener(_ -> new ManageTasksUI(taskService, robotService, pasteUrlService,"USER", userId).setVisible(true));

        panel.add(viewRobotsButton);
        panel.add(viewTasksButton);

        add(panel, BorderLayout.CENTER);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
