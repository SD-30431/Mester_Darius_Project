package com.example.robotmanagement.service;

import com.example.robotmanagement.dto.SystemExport;
import com.example.robotmanagement.repository.*;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

@Service
public class ExportService {
    private final UserRepository userRepo;
    private final RobotRepository robotRepo;
    private final TaskRepository taskRepo;
    private final LoginActivityNotifier loginActivityNotifier;

    public ExportService(UserRepository userRepo, RobotRepository robotRepo, TaskRepository taskRepo, LoginActivityNotifier loginActivityNotifier) {
        this.userRepo = userRepo;
        this.robotRepo = robotRepo;
        this.taskRepo = taskRepo;
        this.loginActivityNotifier = loginActivityNotifier;
    }

    public ByteArrayResource exportAllToXml() throws Exception {
        SystemExport export = new SystemExport();

        export.users = userRepo.findAll().stream().peek(u -> u.setPassword(null)).toList(); // no password
        export.robots = robotRepo.findAll();
        export.tasks = taskRepo.findAll();
        export.loginActivity = loginActivityNotifier.getAll();

        XmlMapper mapper = new XmlMapper();
        mapper.registerModule(new JavaTimeModule());
        String xml = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(export);

        return new ByteArrayResource(xml.getBytes());
    }
}
