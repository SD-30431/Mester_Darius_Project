package com.example.robotmanagement.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Comparator;

public class FileUploader {
    public static void main(String[] args) {
        File downloadsFolder = new File("C://Users//meste//Downloads");

        // Get the most recent file in the Downloads folder
        File latestFile = getMostRecentFile(downloadsFolder);
        if (latestFile == null) {
            System.out.println("No files found in the Downloads folder.");
            return;
        }

        // Find the MICROBIT drive
        File microbitDrive = findMicrobitDrive();
        if (microbitDrive == null) {
            System.out.println("MICROBIT drive not found.");
            return;
        }

        Path destinationPath = new File(microbitDrive, latestFile.getName()).toPath();

        try {
            Files.copy(latestFile.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("File uploaded successfully to " + microbitDrive.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static File getMostRecentFile(File folder) {
        File[] files = folder.listFiles();
        if (files == null || files.length == 0) {
            return null;
        }

        return Arrays.stream(files)
                .filter(File::isFile)
                .max(Comparator.comparingLong(File::lastModified))
                .orElse(null);
    }

    private static File findMicrobitDrive() {
        File[] roots = File.listRoots(); // Get all mounted drives
        for (File root : roots) {
            File microbitTestFile = new File(root, "MICROBIT.HTM"); // A file that always exists in a MICROBIT drive
            if (microbitTestFile.exists()) {
                return root; // Return the drive if MICROBIT is found
            }
        }
        return null;
    }
}
