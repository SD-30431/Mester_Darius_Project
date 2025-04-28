package com.example.robotmanagement.util;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class PastebinUploader {

    private static final String DEV_KEY = "Ax6If3KpwG0Q7FDl4lvMmBeAjGqWD4_T";
    private static final String API_URL = "https://pastebin.com/api/api_post.php";

    public static String uploadCode(String code, String title) throws IOException {
        StringBuilder postData = new StringBuilder();
        postData.append("api_dev_key=").append(URLEncoder.encode(DEV_KEY, StandardCharsets.UTF_8));
        postData.append("&api_option=paste");
        postData.append("&api_paste_code=").append(URLEncoder.encode(code, StandardCharsets.UTF_8));
        postData.append("&api_paste_name=").append(URLEncoder.encode(title, StandardCharsets.UTF_8));
        postData.append("&api_paste_format=javascript");
        postData.append("&api_paste_private=1"); // unlisted
        postData.append("&api_paste_expire_date=N");

        byte[] postBytes = postData.toString().getBytes(StandardCharsets.UTF_8);

        HttpURLConnection conn = (HttpURLConnection) new URL(API_URL).openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", String.valueOf(postBytes.length));

        try (OutputStream os = conn.getOutputStream()) {
            os.write(postBytes);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

    public static String getAICode(String url) {
        try {
            // Extract Paste ID using regex
            String pasteId = url.replaceAll(".*/([\\w]+)$", "$1");

            // Construct raw URL
            String rawUrl = "https://pastebin.com/raw/" + pasteId;

            URL rawURL = new URL(rawUrl);
            HttpURLConnection conn = (HttpURLConnection) rawURL.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder content = new StringBuilder();
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine).append("\n");
            }

            in.close();
            conn.disconnect();

            // Output the paste content
            System.out.println("Paste content:");
            System.out.println(content.toString());

            return content.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void openAICode(String url) {
        // Set path to WebDriver
        System.setProperty("webdriver.chrome.driver", "robotmanagement\\src\\main\\resources\\chromedriver-win64\\chromedriver.exe");

        Path path = Paths.get(System.getProperty("user.home")); // Get C:\Users\YourUser
        String secondFolder = path.getName(1).toString(); // Get "meste"

        ChromeOptions options = new ChromeOptions();
        options.addArguments("user-data-dir=C:\\Users\\" + secondFolder + "\\AppData\\Local\\Google\\Chrome\\User Data");
        options.addArguments("profile-directory=Default");  // Change "Default" to your actual profile name
        WebDriver driver = new ChromeDriver(options);

        try {
            // Open the MakeCode Microbit editor
            driver.get(url);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        //openAICode("https://pastebin.com/dPwrJhaR");
        String testCode = "basic.showString(\"Hello from Pastebin!\")";
        String pasteUrl = uploadCode(testCode,"ceva");
        System.out.println("Paste created at: " + pasteUrl);
        getAICode(pasteUrl);
    }
}
