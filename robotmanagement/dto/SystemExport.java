package com.example.robotmanagement.dto;

import com.example.robotmanagement.entity.User;
import com.example.robotmanagement.entity.Robot;
import com.example.robotmanagement.entity.Task;

import java.util.List;

public class SystemExport {
    public List<User> users;
    public List<Robot> robots;
    public List<Task> tasks;
    public List<LoginActivityMessage> loginActivity;
}
