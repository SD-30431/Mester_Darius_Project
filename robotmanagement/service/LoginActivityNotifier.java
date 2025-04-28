package com.example.robotmanagement.service;

import com.example.robotmanagement.dto.LoginActivityMessage;
import com.example.robotmanagement.websocket.LoginActivityWebSocketHandler;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LoginActivityNotifier {

    private final LoginActivityWebSocketHandler webSocketHandler;
    private final List<LoginActivityMessage> activityLog = new ArrayList<>();

    public LoginActivityNotifier(LoginActivityWebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
    }

    public void notifyLogin(String username) {
        LoginActivityMessage message = new LoginActivityMessage("LOGIN", username);
        activityLog.add(message);
        webSocketHandler.broadcast(message);
    }

    public void notifyLogout(String username) {
        LoginActivityMessage message = new LoginActivityMessage("LOGOUT", username);
        activityLog.add(message);
        webSocketHandler.broadcast(message);
    }

    public List<LoginActivityMessage> getAll() {
        return activityLog;
    }
}
