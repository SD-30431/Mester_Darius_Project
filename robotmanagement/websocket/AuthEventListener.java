package com.example.robotmanagement.websocket;

import com.example.robotmanagement.dto.LoginActivityMessage;
import com.example.robotmanagement.websocket.LoginActivityWebSocketHandler;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.authentication.event.LogoutSuccessEvent;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AuthEventListener {

    private final LoginActivityWebSocketHandler webSocketHandler;

    public AuthEventListener(LoginActivityWebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
    }

    @EventListener
    public void handleLoginSuccess(AuthenticationSuccessEvent event) {
        String username = event.getAuthentication().getName();
        sendActivity("LOGIN", username);
    }

    @EventListener
    public void handleLogoutSuccess(LogoutSuccessEvent event) {
        String username = event.getAuthentication().getName();
        sendActivity("LOGOUT", username);
    }

    private void sendActivity(String action, String username) {
        LoginActivityMessage msg = new LoginActivityMessage(action, username);
        webSocketHandler.broadcast(msg);
    }
}
