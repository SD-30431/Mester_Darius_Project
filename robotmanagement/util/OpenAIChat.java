package com.example.robotmanagement.util;

import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;

public class OpenAIChat {
    private static final String API_KEY = ;
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";

    public static String generateMicrobitCode(String text) throws IOException {
        OkHttpClient client = new OkHttpClient();
        String prompt= "Write javascript code in micro:bit makecode, response only the code, first line = // + short name of program. Use javascript functions that you already know, and also use these implemented functions to generate the missing logic: for motors: motor.servo(motor.Servos.S1, 0), motor.MotorRun(motor.Motors.M1, motor.Dir.CW, 0), motor.motorStop(motor.Motors.M1), motor.motorStopAll(). The left motor is M1 and right is M4, Servo are 1 to 8. function remote() is used for the radio remote (when user wants an remote, you use basic.forever(function () {while (true) {remote()}})). function go_front(), function go_back(), function rotate_to(dir: string) dir = left/right. These function are already implemented, just call and use them as they suggest. Also do not use Pins 0,3,4.";
        // Create the prompt and messages for the chat
        String jsonBody = "{\n" +
                "  \"model\": \"gpt-4o-mini\",\n" +  // Using GPT-4o-mini
                "  \"messages\": [\n" +
                "    {\"role\": \"system\", \"content\": \"" + prompt + "\"},\n" +
                "    {\"role\": \"user\", \"content\": \"" + text + "\"}\n" +
                "  ],\n" +
                "  \"max_tokens\": 1000,\n" +
                "  \"temperature\": 0.7\n" +
                "}";

        RequestBody body = RequestBody.create(jsonBody, MediaType.get("application/json"));

        Request request = new Request.Builder()
                .url(API_URL)
                .header("Authorization", "Bearer " + API_KEY)
                .post(body)
                .build();

        // Make the request
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.out.println("Request failed with code: " + response.code());
            } else {
                assert response.body() != null;
                String responseBody = response.body().string();
                return parseResponse(responseBody);
            }
        }
        return null;
    }

    private static String parseResponse(String jsonResponse) {
        JSONObject jsonObject = new JSONObject(jsonResponse);

        // Extract the message content from the response
        String reply = jsonObject.getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message") // Get the message object
                .getString("content"); // Extract the content field

            String[] lines = reply.split("\n");
            if (lines.length <= 2) {
                reply = ""; // If there are one or two lines, return an empty string.
            } else {
                reply = String.join("\n", java.util.Arrays.copyOfRange(lines, 1, lines.length - 1));
            }

        System.out.println("Assistant: " + reply);
        return reply;
    }
}
