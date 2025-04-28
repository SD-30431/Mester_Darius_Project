package com.example.robotmanagement.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Entity
public class Robot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Robot name must not be blank")
    private String name;

    @NotNull(message = "Owner must not be null")
    @ManyToOne
    @JsonIgnoreProperties({"robots", "password", "authorities", "tasks"}) // Avoid sensitive/circular fields
    private User owner;

    @OneToMany(mappedBy = "robot", cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"robot"})  // Ignore task.robot to prevent back reference
    private List<Task> tasks;

    private LocalDateTime created_at;  // Timestamp for when the robot was created

    public Robot() {
        this.created_at = LocalDateTime.now();
    }
}
