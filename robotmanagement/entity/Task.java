package com.example.robotmanagement.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Entity
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Task description must not be blank")
    private String description;

    @NotBlank(message = "Task name must not be blank")
    private String name;

    @NotNull(message = "Task status must be provided")
    @Enumerated(EnumType.STRING)
    private TaskStatus status; // Task status (PENDING or COMPLETED)

    @NotNull(message = "Task must be assigned to a robot")
    @ManyToOne
    @JsonIgnoreProperties({"tasks"})
    private Robot robot;

    private LocalDateTime created_at;  // Timestamp for when the robot was created
    private String url;

    public Task() {
        this.created_at = LocalDateTime.now();
    }
}
