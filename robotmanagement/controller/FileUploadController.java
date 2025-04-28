package com.example.robotmanagement.controller;

import com.example.robotmanagement.util.JwtUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.core.io.ByteArrayResource;

import java.util.Map;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin(origins = "http://localhost:4200") // Allow frontend to talk to backend
public class FileUploadController {

    private final RestTemplate restTemplate = new RestTemplate();
    private final JwtUtil jwtUtil; // Inject JwtUtil for extracting username from the token

    public FileUploadController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) throws Exception {

        // Validate the token manually if needed (optional, because JwtFilter already does it)
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(Map.of("error", "Missing or invalid Authorization header"));
        }

        // Extract the username from the JWT token
        String token = authorizationHeader.substring(7);  // Remove "Bearer " prefix
        String username = jwtUtil.extractUsername(token);

        if (username == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid token or user not found"));
        }

        // Create the bin name dynamically with the username
        String bin = "RobotManagement_" + username;
        String filename = file.getOriginalFilename();
        String url = "https://filebin.net/" + bin + "/" + filename;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        ByteArrayResource resource = new ByteArrayResource(file.getBytes());

        HttpEntity<ByteArrayResource> requestEntity = new HttpEntity<>(resource, headers);

        // Send file to the filebin URL
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

        // Return a clean JSON response with the uploaded URL
        return ResponseEntity.status(response.getStatusCode()).body(
                Map.of(
                        "message", "Upload successful",
                        "uploadedUrl", url
                )
        );
    }
}
