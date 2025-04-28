package com.example.robotmanagement.ui;

import com.example.robotmanagement.entity.User;
import com.example.robotmanagement.service.AdminService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ManageUsersUI extends JFrame {
    private final AdminService adminService;
    private final DefaultListModel<String> listModel;
    private final JList<String> userList;

    public ManageUsersUI(AdminService adminService) {
        this.adminService = adminService;
        setTitle("Manage Users");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        listModel = new DefaultListModel<>();
        userList = new JList<>(listModel);
        refreshList();

        JButton addButton = new JButton("Add User");
        JButton editButton = new JButton("Edit User");
        JButton deleteButton = new JButton("Delete User");

        addButton.addActionListener(_ -> addUser());
        editButton.addActionListener(_ -> editUser());
        deleteButton.addActionListener(_ -> deleteUser());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        add(new JScrollPane(userList), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void refreshList() {
        listModel.clear();
        List<User> users = adminService.getAllUsers();
        for (User user : users) {
            listModel.addElement(user.getId() + " - " + user.getUsername() + " - " + user.getRole() + " - " + user.getCreated_at());
        }
    }

    private void addUser() {
        String username = JOptionPane.showInputDialog(this, "Enter username:");
        if (username != null && !username.isEmpty()) {
            String password = JOptionPane.showInputDialog(this, "Enter password:");
            if (password != null && !password.isEmpty()) {
                String role = JOptionPane.showInputDialog(this, "Enter role (e.g., ADMIN or USER):");
                if (role != null && !role.isEmpty()) {
                    User user = new User();
                    user.setUsername(username);
                    user.setPassword(password);
                    user.setRole(User.Role.valueOf(role));
                    adminService.createUser(user);
                    refreshList();
                } else {
                    JOptionPane.showMessageDialog(this, "Role cannot be empty.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Password cannot be empty.");
            }
        }
    }

    private void editUser() {
        int selectedIndex = userList.getSelectedIndex();
        if (selectedIndex != -1) {
            String selectedValue = userList.getSelectedValue();
            Long id = Long.parseLong(selectedValue.split(" - ")[0]);
            User user = adminService.getUserById(id);

            String newUsername = JOptionPane.showInputDialog(this, "Enter new username:", user.getUsername());
            if (newUsername != null && !newUsername.isEmpty()) {
                String newPassword = JOptionPane.showInputDialog(this, "Enter new password:", user.getPassword());
                if (newPassword != null && !newPassword.isEmpty()) {
                    String newRole = JOptionPane.showInputDialog(this, "Enter new role:", user.getRole());
                    if (newRole != null && !newRole.isEmpty()) {
                        user.setUsername(newUsername);
                        user.setPassword(newPassword);
                        user.setRole(User.Role.valueOf(newRole));
                        adminService.updateUser(id, user);
                        refreshList();
                    } else {
                        JOptionPane.showMessageDialog(this, "Role cannot be empty.");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Password cannot be empty.");
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a user to edit.");
        }
    }

    private void deleteUser() {
        int selectedIndex = userList.getSelectedIndex();
        if (selectedIndex != -1) {
            String selectedValue = userList.getSelectedValue();
            Long id = Long.parseLong(selectedValue.split(" - ")[0]);
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this user?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                adminService.deleteUser(id);
                refreshList();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a user to delete.");
        }
    }
}
