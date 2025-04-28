package com.example.robotmanagement.controller;

import com.example.robotmanagement.entity.User;
import com.example.robotmanagement.repository.UserRepository;
import com.example.robotmanagement.service.AuthService;
import com.example.robotmanagement.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200") // Allow frontend URL
public class AuthController {
    private final AuthService authService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(AuthService authService, UserRepository userRepository,
                          PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.authService = authService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        String role = body.get("role");

        if (userRepository.findByUsername(username).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(User.Role.valueOf(role.toUpperCase()));
        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        // Authenticate the user using AuthService
        User user = authService.authenticate(username, password);
        System.out.println("Received username: " + username);  // Debugging log

        if (user == null) {
            System.out.println("Invalid credentials");  // Debugging log
            return ResponseEntity.badRequest().body("Invalid credentials");
        }

        // Generate the JWT token
        String token = jwtUtil.generateToken(username);

        // Retrieve the user's role
        String role = user.getRole().toString();  // Assuming the User entity has a Role enum

        // Return the token and role in the response
        return ResponseEntity.ok(Map.of(
                "token", token,
                "role", role,
                "username", user.getUsername(),
                "id", user.getId(),
                "created_at", user.getCreated_at().toString()
        ));
    }


}
