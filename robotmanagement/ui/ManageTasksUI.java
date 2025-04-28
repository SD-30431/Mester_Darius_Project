package com.example.robotmanagement.ui;

import com.example.robotmanagement.entity.Robot;
import com.example.robotmanagement.entity.Task;
import com.example.robotmanagement.service.PasteUrlService;
import com.example.robotmanagement.util.OpenAIChat;
import com.example.robotmanagement.entity.TaskStatus;
import com.example.robotmanagement.service.TaskService;
import com.example.robotmanagement.service.RobotService;
import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

import com.example.robotmanagement.util.PastebinUploader;
import com.google.cloud.speech.v1.*;
import com.google.protobuf.ByteString;
import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.example.robotmanagement.util.MicrobitDownloadAutomation.deployCodeToMicrobit;
import static com.example.robotmanagement.util.MicrobitDownloadAutomation.openCode;
import static com.example.robotmanagement.util.PastebinUploader.getAICode;
import static com.example.robotmanagement.util.PastebinUploader.openAICode;

public class ManageTasksUI extends JFrame {
    private final TaskService taskService;
    private final RobotService robotService;
    private final PasteUrlService pasteUrlService;
    private final DefaultListModel<String> listModel;
    private final JList<String> taskList;
    private final String currentUserRole;
    private final Long currentUserId;


    public ManageTasksUI(TaskService taskService, RobotService robotService, PasteUrlService pasteUrlService, String currentUserRole, Long currentUserId) {
        this.taskService = taskService;
        this.robotService = robotService;
        this.pasteUrlService = pasteUrlService;
        this.currentUserRole = currentUserRole;
        this.currentUserId = currentUserId;


        setTitle("Manage Tasks");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        listModel = new DefaultListModel<>();
        taskList = new JList<>(listModel);
        refreshList();

        JButton addButton = new JButton("Add Task");
        JButton addAIButton = new JButton("Add Shared Code");
        JButton editButton = new JButton("Edit Task");
        JButton deleteButton = new JButton("Delete Task");
        JButton codeButton = new JButton("Micro:Bit Code");
        JButton codeAIButton = new JButton("JS Code");
        JButton broadcastButton = new JButton("Share Code");

        addButton.addActionListener(_ -> {
            try {
                addTask();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        addAIButton.addActionListener(_ -> {
            try {
                addAITask();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        editButton.addActionListener(_ -> editTask());
        deleteButton.addActionListener(_ -> deleteTask());
        editButton.addActionListener(_ -> editTask());
        codeButton.addActionListener(_ -> codeTask());
        codeAIButton.addActionListener(_ -> codeAITask());
        broadcastButton.addActionListener(_ -> broadcastTask());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(addAIButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(codeButton);
        buttonPanel.add(codeAIButton);
        buttonPanel.add(broadcastButton);

        add(new JScrollPane(taskList), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void refreshList() {
        listModel.clear();
        List<Task> tasks;

        if (currentUserRole.equals("ADMIN")) {
            tasks = taskService.getAllTasks();
        } else {
            tasks = taskService.getTasksForUserRobots(currentUserId);
        }

        for (Task task : tasks) {
            listModel.addElement(task.getId() + " - " + task.getName() +" - "+ task.getDescription() + task.getUrl() +" (" + task.getStatus() + ") - Robot ID: " + task.getRobot().getId());
        }
    }

    public static String getFirstLine(String text) {
        int newLineIndex = text.indexOf('\n');
        return (newLineIndex == -1) ? text : text.substring(0, newLineIndex);
    }

    private void codeTask(){
        int selectedIndex = taskList.getSelectedIndex();
        if (selectedIndex != -1) {
            String selectedValue = taskList.getSelectedValue();
            String name = selectedValue.split(" - ")[1]; // Assuming name is after the first " - "
            openCode(name);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a task", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void codeAITask(){
        int selectedIndex = taskList.getSelectedIndex();
        if (selectedIndex != -1) {
            String selectedValue = taskList.getSelectedValue();
            String url = selectedValue.split(" - ")[3]; // Assuming name is after the first " - "
            openAICode(url);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a task", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addTask() throws IOException {
        String description = JOptionPane.showInputDialog(this, "Enter task description (or click OK for Speech to Text):");

        List<Robot> availableRobots = robotService.getRobotsByOwnerId(currentUserId);
        if (availableRobots.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No robots available.");
            return;
        }

        if (description == null || description.isEmpty()) {
            description = startSpeechToText();  // Step 1: Capture Speech
        }
        if (description == null || description.isEmpty()) return;

        // Step 2: Generate Code from AI
        String generatedCode = OpenAIChat.generateMicrobitCode(description);
        if (generatedCode == null) {
            JOptionPane.showMessageDialog(this, "AI code generation failed.");
            return;
        }
        //get the name
        String name = getFirstLine(generatedCode);
        // Step 3: Send Code to Micro:bit
        // Create the new code to be inserted
        String insertCode = "radio.setGroup(" + (currentUserId + 10) + ")\n";
        // Split the original generatedCode by newlines
        String[] lines = generatedCode.split("\n");
        // Reconstruct the generatedCode with the inserted line
        StringBuilder modifiedCode = new StringBuilder();
        modifiedCode.append(lines[0]) // Append the first line
                .append("\n")
                .append(insertCode) // Insert the radio.setGroup line
                .append(String.join("\n", Arrays.copyOfRange(lines, 1, lines.length))); // Append the remaining lines

        deployCodeToMicrobit(String.valueOf(modifiedCode), name);
        // Step 4: Create Task in Database
        Task task = new Task();
        task.setName(name);
        task.setDescription(description);
        task.setStatus(TaskStatus.PENDING);

        task.setUrl(PastebinUploader.uploadCode(String.valueOf(modifiedCode), name));

        String[] robotOptions = availableRobots.stream()
                .map(robot -> "ID: " + robot.getId() + " - " + robot.getName())
                .toArray(String[]::new);

        String selectedRobot = (String) JOptionPane.showInputDialog(this, "Select a robot:", "Assign Task",
                JOptionPane.QUESTION_MESSAGE, null, robotOptions, robotOptions[0]);

        if (selectedRobot == null) return;

        Long robotId = Long.parseLong(selectedRobot.split(" - ")[0].replace("ID: ", "").trim());
        task.setRobot(robotService.getRobotById(robotId));

        taskService.createTask(task);
        refreshList();
    }

    private void addAITask() throws IOException {
        String url = pasteUrlService.getMainUrl(); // use DAO method
        //System.out.println(url);
        List<Robot> availableRobots = robotService.getRobotsByOwnerId(currentUserId);
        if (availableRobots.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No robots available.");
            return;
        }
        String generatedCode = getAICode(url);
        assert generatedCode != null;
        String name = getFirstLine(generatedCode);

        deployCodeToMicrobit(generatedCode, name);

        Task task = new Task();
        task.setName(name);
        task.setDescription("Code from sharing");
        task.setStatus(TaskStatus.PENDING);
        task.setUrl(url);
        String[] robotOptions = availableRobots.stream()
                .map(robot -> "ID: " + robot.getId() + " - " + robot.getName())
                .toArray(String[]::new);

        String selectedRobot = (String) JOptionPane.showInputDialog(this, "Select a robot:", "Assign Task",
                JOptionPane.QUESTION_MESSAGE, null, robotOptions, robotOptions[0]);

        if (selectedRobot == null) return;

        Long robotId = Long.parseLong(selectedRobot.split(" - ")[0].replace("ID: ", "").trim());
        task.setRobot(robotService.getRobotById(robotId));

        taskService.createTask(task);
        refreshList();
    }

    private void broadcastTask() {
        int selectedIndex = taskList.getSelectedIndex();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(this, "Please select a task to edit.");
            return;
        }

        String selectedValue = taskList.getSelectedValue();
        Long id = Long.parseLong(selectedValue.split(" - ")[0]);

        Task task = taskService.getTaskById(id);
        System.out.println(task.getUrl());
        pasteUrlService.updateMainUrl(task.getUrl());
    }

    // Method to capture audio and convert speech to text using CMU Sphinx
    private String startSpeechToText() {
        try (SpeechClient speechClient = SpeechClient.create()) {
            // Capture audio from microphone
            byte[] audioData = captureAudio();
            if (audioData == null) {
                JOptionPane.showMessageDialog(this, "Failed to capture audio.");
                return null;
            }

            // Convert audio to Google Cloud format
            RecognitionConfig config = RecognitionConfig.newBuilder()
                    .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                    .setSampleRateHertz(16000)
                    .setLanguageCode("en-US")
                    .build();

            RecognitionAudio audio = RecognitionAudio.newBuilder()
                    .setContent(ByteString.copyFrom(audioData))
                    .build();

            // Send request to Google Cloud Speech-to-Text
            RecognizeResponse response = speechClient.recognize(config, audio);
            if (!response.getResultsList().isEmpty()) {
                return response.getResultsList().get(0).getAlternatives(0).getTranscript();
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Speech recognition failed: " + e.getMessage());
        }
        return null;
    }

    public byte[] captureAudio() {
        AtomicBoolean isRecording = new AtomicBoolean(false);
        try {
            AudioFormat format = new AudioFormat(16000, 16, 1, true, false);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

            if (!AudioSystem.isLineSupported(info)) {
                JOptionPane.showMessageDialog(null, "Microphone not supported.");
                return null;
            }

            TargetDataLine microphone = (TargetDataLine) AudioSystem.getLine(info);
            microphone.open(format);
            microphone.start();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];

            isRecording.set(true); // Set recording flag

            // Start a separate thread for recording
            Thread recordingThread = new Thread(() -> {
                while (isRecording.get()) {  // Stop when flag is set to false
                    int bytesRead = microphone.read(buffer, 0, buffer.length);
                    out.write(buffer, 0, bytesRead);
                }
            });

            recordingThread.start();

            // Show JOptionPane and wait for the user to click OK
            JOptionPane.showMessageDialog(null, "Recording... Click OK to stop.");

            // Stop recording when the user clicks OK
            isRecording.set(false);
            microphone.stop();
            microphone.close();

            // Wait for the recording thread to finish
            recordingThread.join();

            return out.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private void editTask() {
        int selectedIndex = taskList.getSelectedIndex();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(this, "Please select a task to edit.");
            return;
        }

        String selectedValue = taskList.getSelectedValue();
        Long id = Long.parseLong(selectedValue.split(" - ")[0]);

        Task task = taskService.getTaskById(id);

        // Check permission to edit robot ID
        if (!currentUserRole.equals("ADMIN") && !task.getRobot().getOwner().getId().equals(currentUserId)) {
            JOptionPane.showMessageDialog(this, "You can only edit tasks for your own robots.");
            return;
        }

        // Edit description
        String newDesc = JOptionPane.showInputDialog(this, "Enter new description:", task.getDescription());
        if (newDesc != null && !newDesc.isEmpty()) {
            task.setDescription(newDesc);
        }

        // Edit status
        String newStatusString = JOptionPane.showInputDialog(this, "Enter new status (e.g., PENDING, IN_PROGRESS, COMPLETED):", task.getStatus());
        if (newStatusString != null && !newStatusString.isEmpty()) {
            try {
                TaskStatus newStatus = TaskStatus.valueOf(newStatusString.toUpperCase());
                task.setStatus(newStatus);
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this, "Invalid status.");
                return;
            }
        }

        // Only Admin can change robot ID and Url
        if (currentUserRole.equals("ADMIN")) {
            String newUrl = JOptionPane.showInputDialog(this, "Enter new description:", task.getDescription());
            if (newDesc != null && !newDesc.isEmpty()) {
                task.setDescription(newUrl);
            }
            String robotSelection = JOptionPane.showInputDialog(this, "Enter new robot ID (Leave empty to keep existing):");
            if (robotSelection != null && !robotSelection.isEmpty()) {
                Long robotId = Long.parseLong(robotSelection.trim());
                task.setRobot(robotService.getRobotById(robotId));
            }
        }

        taskService.updateTask(id, task);
        refreshList();
    }

    private void deleteTask() {
        int selectedIndex = taskList.getSelectedIndex();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(this, "Please select a task to delete.");
            return;
        }

        String selectedValue = taskList.getSelectedValue();
        Long id = Long.parseLong(selectedValue.split(" - ")[0]);

        Task task = taskService.getTaskById(id);

        // Check permission
        if (!currentUserRole.equals("ADMIN") && !task.getRobot().getOwner().getId().equals(currentUserId)) {
            JOptionPane.showMessageDialog(this, "You can only delete tasks for your own robots.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this task?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            taskService.deleteTask(id);
            refreshList();
        }
    }
}
