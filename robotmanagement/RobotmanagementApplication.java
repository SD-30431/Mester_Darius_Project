package com.example.robotmanagement;

import com.example.robotmanagement.entity.User.Role;
import com.example.robotmanagement.entity.User;
import com.example.robotmanagement.repository.UserRepository;
import com.example.robotmanagement.service.*;
import com.example.robotmanagement.ui.LoginUI;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.swing.*;

import java.io.IOException;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.example.robotmanagement.repository")
@EntityScan(basePackages = "com.example.robotmanagement.entity")
public class RobotmanagementApplication {

	private static ApplicationContext context;

	public static void main(String[] args) throws IOException, InterruptedException {
		System.setProperty("java.awt.headless", "false");

		// Start Spring Boot context
		context = SpringApplication.run(RobotmanagementApplication.class, args);

		// Ensure first admin user exists
		createAdmin(context.getBean(UserRepository.class));

		// Launch UI after admin is created
		SwingUtilities.invokeLater(() -> {
			AuthService authService = context.getBean(AuthService.class);
			new LoginUI(authService);
		});
	}

	private static void createAdmin(UserRepository userRepository) {
		if (userRepository.count() == 0) {
			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			User admin = new User();
			admin.setUsername("admin");
			admin.setPassword(passwordEncoder.encode("admin123")); // Default admin password
			admin.setRole(Role.ADMIN);
			userRepository.save(admin);
			System.out.println("First admin created: username=admin, password=admin123");
		}
	}

	public static AdminService getAdminService() {
		return context.getBean(AdminService.class);
	}

	public static AuthService getAuthService() {
		return context.getBean(AuthService.class);
	}

	public static RobotService getRobotService() {
		return context.getBean(RobotService.class);
	}

	public static TaskService getTaskService() {
		return context.getBean(TaskService.class);
	}

	public static PasteUrlService getPasteUrlService() {
		return context.getBean(PasteUrlService.class);
	}
}
