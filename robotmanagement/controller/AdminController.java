package com.example.robotmanagement.controller;

import com.example.robotmanagement.service.ExportService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:4200")
public class AdminController {

    private final ExportService exportService;

    public AdminController(ExportService exportService) {
        this.exportService = exportService;
    }

    @GetMapping("/export/xml")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Resource> exportXml() throws Exception {
        ByteArrayResource xml = exportService.exportAllToXml();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=data.xml")
                .contentType(MediaType.APPLICATION_XML)
                .body(xml);
    }
}
