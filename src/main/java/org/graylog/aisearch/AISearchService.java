package org.graylog.aisearch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Scanner;

public class AISearchService {
    private static final Logger LOG = LoggerFactory.getLogger(AISearchService.class);

    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String API_KEY = "API_KEY_HERE";

    // 1) Add this toggle to decide whether to skip calling Graylog's API and just read from file.
    private static final boolean USE_TEST_LOGS = false;

    private static final Path LOG_FILE_PATH = Paths.get("/logs/OpenSSH_2k_LF.log"); // Your "test logs"

    /**
     * Constructor: Spin up a background thread that either:
     * (A) If USE_TEST_LOGS == true, skip Graylog entirely and read from LOG_FILE_PATH.
     * (B) Otherwise, keep retrying until Graylog is up, then call fetchLogsFromGraylog().
     */
    public AISearchService() {
        /*new Thread(() -> {
            if (USE_TEST_LOGS) {
                // 2) If USE_TEST_LOGS == true, skip Graylog checks. Just read file & call OpenAI.
                LOG.info("USE_TEST_LOGS == true, skipping Graylog. Reading file-based logs only...");

                // Read the test logs from the file
                String fileLogs = readLogFile(LOG_FILE_PATH.toString());

                // Log each line
                try (Scanner scanner = new Scanner(fileLogs)) {
                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        LOG.info("Test log line: {}", line);
                    }
                }

                // Send them to OpenAI
                String openAiResponse = callOpenAI(fileLogs);
                LOG.info("Test logs -> OpenAI response: {}", openAiResponse);

            } else {
                // 3) Original logic: keep retrying until Graylog is up, then fetch logs.
                int maxRetries = 10000;  // Adjust as needed
                int retryDelayMs = 1000; // Wait 1 second between attempts
                boolean graylogIsUp = false;

                for (int attempt = 1; attempt <= maxRetries; attempt++) {
                    if (isGraylogUp()) {
                        LOG.info("Graylog is reachable on attempt #{}, calling fetchLogsFromGraylog...", attempt);
                        String response = fetchLogsFromGraylog();
                        LOG.info("First attempt to fetch logs after Graylog responded OK: {}", response);
                        graylogIsUp = true;
                        break;
                    } else {
                        LOG.warn("Graylog not reachable on attempt #{}. Will retry in {} ms...", attempt, retryDelayMs);
                        try {
                            Thread.sleep(retryDelayMs);
                        } catch (InterruptedException ignored) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                }

                if (!graylogIsUp) {
                    LOG.error("Exceeded max retries ({}). Graylog still not reachable. Not calling fetchLogsFromGraylog().", maxRetries);
                }
            }
        }).start();*/
    }

    public String doFetchLogic() {
        if (USE_TEST_LOGS) {
            String fileLogs = readLogFile("/logs/OpenSSH_2k_LF.log");
            return callOpenAI(fileLogs);
        } else {
            return fetchLogsFromGraylog(); // same method you have
        }
    }



    /**
     * Quick check to see if Graylog is listening at http://localhost:9000.
     * If we get a 200 (OK) from a HEAD request, assume Graylog is up.
     */
    private boolean isGraylogUp() {
        HttpURLConnection connection = null;
        try {
            URL url = new URL("http://localhost:9000");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(2000);
            connection.setReadTimeout(2000);

            int responseCode = connection.getResponseCode();
            return (responseCode == HttpURLConnection.HTTP_OK);
        } catch (IOException e) {
            // If we hit an IOException, it's not up yet
            return false;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * Main high-level method that:
     *  1) Calls fetchLogsFromGraylogAPI()
     *  2) Reads logs from /logs/OpenSSH_2k_LF.log
     *  3) Calls OpenAI API with the log content
     */
    public String fetchLogsFromGraylog() {


        String graylogData = fetchLogsFromGraylogAPI();

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

    /**
     * Fetch logs from the Graylog API (Equivalent to cURL request)
     */
    public String fetchLogsFromGraylogAPI() {
        try {
            String graylogApiUrl = "http://localhost:9000/api/search/messages?fields=timestamp,source,message&size=10";

            URL url = new URL(graylogApiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Set headers
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("X-Requested-By", "cli");

            // Basic Auth
            String encodedAuth = Base64.getEncoder()
                    .encodeToString("admin:admin".getBytes(StandardCharsets.UTF_8));
            connection.setRequestProperty("Authorization", "Basic " + encodedAuth);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        response.append(line);
                    }
                    LOG.info("Graylog API Response: {}", response);
                    return response.toString();
                }
            } else {
                LOG.error("Failed to fetch logs from Graylog API. Response Code: {}", responseCode);
                return "Error: Failed to fetch logs.";
            }
        } catch (Exception e) {
            LOG.error("Error fetching logs from Graylog API: {}", e.getMessage(), e);
            return "Error: " + e.getMessage();
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

            // Replace newlines with spaces to avoid JSON parse issues
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
              "max_tokens": 5000,
              "top_p": 1.0,
              "frequency_penalty": 0.0,
              "presence_penalty": 0.0
            }
            """.formatted(input);

            try (OutputStream os = connection.getOutputStream()) {
                os.write(payload.getBytes(StandardCharsets.UTF_8));
                os.flush();
            }

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
                    LOG.error("OpenAI API Response Body: {}", errorResponse);
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
