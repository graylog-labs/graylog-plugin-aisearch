package org.graylog.aisearch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class AISearchService {
    private static final Logger LOG = LoggerFactory.getLogger(AISearchService.class);

    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String API_KEY = "API KEY HERE"; // Replace with your actual OpenAI API key
    private static final Path LOG_FILE_PATH = Paths.get("/logs/OpenSSH_2k_LF.log"); // Update with your desired log file

    public String fetchLogsFromGraylog() {
        // Read the log data from the file
        String graylogData = readLogFile(String.valueOf(LOG_FILE_PATH));

        // Log each line of the read logs for debugging
        try (Scanner scanner = new Scanner(graylogData)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                LOG.info("Read line: {}", line);
            }
        }

        // Call OpenAI API with the log data
        return callOpenAI(graylogData);
    }

    public String readLogFile(String filePath) {
        try {
            LOG.info("Attempting to read log file from path: {}", filePath);
            Path path = Paths.get(filePath);

            if (!Files.exists(path)) {
                LOG.error("Log file does not exist: {}", filePath);
                return "Error: Log file does not exist.";
            }

            return Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            LOG.error("Error reading log file: {}", e.getMessage(), e);
            return "Error: Unable to read log file.";
        }
    }

    private String callOpenAI(String input) {
        try {
            // Set up the OpenAI API connection
            URL url = new URL(OPENAI_API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + API_KEY);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            input = input.replace("\n", "    ");


            // Prepare the request payload
            String payload = """
            {
              "model": "gpt-4o-mini",
              "messages": [
                {"role": "system", "content": "You are a helpful assistant analyzing logs."},
                {"role": "user", "content": "%s"}
              ],
              "temperature": 0.7,
              "max_tokens": 150,
              "top_p": 1.0,
              "frequency_penalty": 0.0,
              "presence_penalty": 0.0
            }
            """.formatted(input);



            // Send the request
            try (OutputStream os = connection.getOutputStream()) {
                os.write(payload.getBytes(StandardCharsets.UTF_8));
                os.flush();
            }

            // Handle the response
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        response.append(line);
                    }
                    LOG.info("OpenAI API Response: {}", response);
                    return response.toString();
                }
            } else {
                // Log error response body for debugging
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8))) {
                    StringBuilder errorResponse = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        errorResponse.append(line);
                    }
                    LOG.error("Failed to fetch from OpenAI API. Response Code: {}", responseCode);
                    LOG.error("OpenAI API Response Body: {}", errorResponse.toString());
                } catch (IOException e) {
                    LOG.error("Failed to read OpenAI API error response: {}", e.getMessage(), e);
                }

                return "Error: Failed to process AI request.";
            }
        } catch (Exception e) {
            LOG.error("Error calling OpenAI API: ", e);
            return "Error: " + e.getMessage();
        }
    }
}
