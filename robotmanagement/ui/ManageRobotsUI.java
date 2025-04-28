package com.example.robotmanagement.ui;

import com.example.robotmanagement.entity.Robot;
import com.example.robotmanagement.service.RobotService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ManageRobotsUI extends JFrame {
    private final RobotService robotService;
    private final DefaultListModel<String> listModel;
    private final JList<String> robotList;
    private final String currentUserRole;
    private final String currentUsername;
    private final Long currentUserId;

    public ManageRobotsUI(RobotService robotService, String currentUserRole, String currentUsername, Long currentUserId) {
        this.robotService = robotService;
        this.currentUserRole = currentUserRole;
        this.currentUsername = currentUsername;
        this.currentUserId = currentUserId;

        setTitle("Manage Robots");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        listModel = new DefaultListModel<>();
        robotList = new JList<>(listModel);
        refreshList();

        JButton addButton = new JButton("Add Robot");
        JButton editButton = new JButton("Edit Robot");
        JButton deleteButton = new JButton("Delete Robot");

        addButton.addActionListener(_ -> addRobot());
        editButton.addActionListener(_ -> editRobot());
        deleteButton.addActionListener(_ -> deleteRobot());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        // Show edit and delete buttons only for ADMIN role
        //if (currentUserRole.equals("ADMIN")) {
            add(buttonPanel, BorderLayout.SOUTH);//}

        add(new JScrollPane(robotList), BorderLayout.CENTER);
    }

    private void refreshList() {
        listModel.clear();
        List<Robot> robots;
        if (currentUserRole.equals("ADMIN"))
            robots=robotService.getAllRobots();
        else
            robots=robotService.getRobotsByOwnerId(currentUserId);
        // Add details (id, name, owner_id, created_at) for each robot to the list
        for (Robot robot : robots) {
            listModel.addElement(robot.getId() + " - " + robot.getName() + " - " + robot.getOwner().getId() + " - " + robot.getCreated_at());
        }
    }

    private void addRobot() {
        String name = JOptionPane.showInputDialog(this, "Enter robot name:");
        if (name != null && !name.isEmpty()) {
            String ownerIdentifier = currentUsername; // Default for normal users

            if (currentUserRole.equals("ADMIN")) {
                ownerIdentifier = JOptionPane.showInputDialog(this, "Enter owner (User ID or Username):");
                if (ownerIdentifier == null || ownerIdentifier.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Owner ID or Username cannot be empty.");
                    return;
                }
            }

            try {
                Long ownerId = Long.parseLong(ownerIdentifier); // Try parsing as a Long (User ID)
                robotService.createRobot(name, String.valueOf(ownerId)); // Assume `createRobot` has an overload for ID-based assignment
            } catch (NumberFormatException e) {
                robotService.createRobot(name, ownerIdentifier); // Fallback to username
            }

            refreshList();
        }
    }


    private void editRobot() {
        int selectedIndex = robotList.getSelectedIndex();
        if (selectedIndex != -1) {
            String selectedValue = robotList.getSelectedValue();
            // Extract ID from the string (ID is always the first part before the ' - ' character)
            String[] parts = selectedValue.split(" - ");
            Long id = Long.parseLong(parts[0].trim()); // Extract ID from "ID: <id>"

            Robot robot = robotService.getRobotById(id);

            // If ADMIN, allow modification of USER_ID
            if (currentUserRole.equals("ADMIN")) {
                String newUserId = JOptionPane.showInputDialog(this, "Enter new User ID:", robot.getOwner().getId());
                if (newUserId != null && !newUserId.isEmpty()) {
                    Long newOwnerId = Long.parseLong(newUserId);
                    robot.getOwner().setId(newOwnerId);  // Update the user ID for ADMIN
                }
            }

            String newName = JOptionPane.showInputDialog(this, "Enter new name:", robot.getName());
            if (newName != null && !newName.isEmpty()) {
                robot.setName(newName);
                robotService.updateRobot(id, robot);
                refreshList();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a robot to edit.");
        }
    }

    private void deleteRobot() {
        int selectedIndex = robotList.getSelectedIndex();
        if (selectedIndex != -1) {
            String selectedValue = robotList.getSelectedValue();
            // Extract ID from the string (ID is always the first part before the ' - ' character)
            String[] parts = selectedValue.split(" - ");
            Long id = Long.parseLong(parts[0].trim()); // Extract robot ID

            Robot robot = robotService.getRobotById(id);

            // Check if the current user is the owner of the robot
            if (!robot.getOwner().getId().equals(currentUserId) && !currentUserRole.equals("ADMIN")) {
                JOptionPane.showMessageDialog(this, "You can only delete your own robots.");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this robot?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                robotService.deleteRobot(id);
                refreshList();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a robot to delete.");
        }
    }
}