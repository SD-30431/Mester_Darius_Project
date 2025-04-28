package com.example.robotmanagement.service;

import com.example.robotmanagement.entity.User;
import com.example.robotmanagement.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final LoginActivityNotifier loginActivityNotifier;

    public AuthService(UserRepository userRepository, LoginActivityNotifier loginActivityNotifier) {
        this.userRepository = userRepository;
        this.loginActivityNotifier = loginActivityNotifier;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public User authenticate(String username, String rawPassword) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent() && passwordEncoder.matches(rawPassword, userOpt.get().getPassword())) {
            // On successful login
            loginActivityNotifier.notifyLogin(username);
            return userOpt.get();  // Return the user if authentication is successful
        }
        return null;  // Return null if authentication fails
    }
}
