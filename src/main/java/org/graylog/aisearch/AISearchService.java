package org.graylog.aisearch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;

import java.nio.charset.StandardCharsets;


public class AISearchService {
    private static final Logger LOG = LoggerFactory.getLogger(AISearchService.class);

    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String API_KEY = "API KET HERE"; // Replace with your actual OpenAI API key

    public String fetchLogsFromGraylog() {
        // Placeholder: Logic to fetch logs from Graylog (mock data for now)
        String graylogData = "Sample logs for AI analysis.";

        // Call OpenAI API
        return callOpenAI(graylogData);
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

            // Prepare the request payload
            String payload = """
            {
              "model": "gpt-4",
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
                    LOG.info("OpenAI API Response: " + response);
                    return response.toString();
                }
            } else {
                LOG.error("Failed to fetch from OpenAI API. Response Code: " + responseCode);
                return "Error: Failed to process AI request.";
            }
        } catch (Exception e) {
            LOG.error("Error calling OpenAI API: ", e);
            return "Error: " + e.getMessage();
        }
    }
}
