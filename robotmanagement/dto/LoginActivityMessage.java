package com.example.robotmanagement.dto;

import java.time.LocalDateTime;

public class LoginActivityMessage {
    private String action;
    private String username;

    public LoginActivityMessage(String action, String username) {
        this.action = action;
        this.username = username;
    }

    public String getAction() {
        return action;
    }

    public String getUsername() {
        return username;
    }
}
