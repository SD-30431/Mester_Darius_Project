package com.example.robotmanagement.service;

import com.example.robotmanagement.entity.Robot;
import com.example.robotmanagement.entity.User;
import com.example.robotmanagement.repository.RobotRepository;
import com.example.robotmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RobotService {
    private final RobotRepository robotRepository;
    private final UserRepository userRepository;

    @Autowired
    public RobotService(RobotRepository robotRepository, UserRepository userRepository) {
        this.robotRepository = robotRepository;
        this.userRepository = userRepository;
    }

    public Long getUserIdByRobotId(Long robotId) {
        return robotRepository.findUserIdByRobotId(robotId);
    }

    public List<Robot> getAllRobots() {
        return robotRepository.findAll();
    }

    public List<Robot> getRobotsByOwnerId(Long ownerId) {
        return robotRepository.findByOwnerId(ownerId);
    }

    public Robot getRobotById(Long id) {
        return robotRepository.findById(id).orElse(null);
    }

    public void createRobot(String name, String username) {
        User owner = userRepository.findByUsername(username).orElse(null);
        if (owner != null) {
            Robot robot = new Robot();
            robot.setName(name);
            robot.setOwner(owner);
            robotRepository.save(robot);
        } else {
            throw new RuntimeException("User not found: " + username);
        }
    }

    public void updateRobot(Long id, Robot updatedRobot) {
        if (robotRepository.existsById(id)) {
            robotRepository.save(updatedRobot);
        }
    }

    public void deleteRobot(Long id) {
        robotRepository.deleteById(id);
    }
}
