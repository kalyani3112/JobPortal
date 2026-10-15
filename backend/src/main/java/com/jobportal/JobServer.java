
package com.jobportal;

import com.jobportal.model.Job;
import com.jobportal.service.JobService;
import com.jobportal.service.UserService;
import com.jobportal.service.ApplicationService;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class JobServer {

    public static void main(String[] args) throws IOException {

        JobService jobService = new JobService();
        UserService userService = new UserService();
        ApplicationService applicationService = new ApplicationService();

        HttpServer server = HttpServer.create(
                new InetSocketAddress(8080), 0);

        // =====================================
        // GET ALL JOBS
        // =====================================
        server.createContext("/api/jobs", exchange -> {
            addCorsHeaders(exchange);

            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendResponse(exchange, 204, "");
                return;
            }

            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendResponse(exchange, 405,
                        "{\"message\":\"Method not allowed\"}");
                return;
            }

            List<Job> jobs = jobService.getAllJobs();
            StringBuilder json = new StringBuilder("[");

            for (int i = 0; i < jobs.size(); i++) {
                Job job = jobs.get(i);

                if (i > 0) {
                    json.append(",");
                }

                json.append("{")
                    .append("\"id\":").append(job.getId()).append(",")
                    .append("\"title\":\"")
                    .append(escapeJson(job.getTitle())).append("\",")
                    .append("\"company\":\"")
                    .append(escapeJson(job.getCompany())).append("\",")
                    .append("\"location\":\"")
                    .append(escapeJson(job.getLocation())).append("\",")
                    .append("\"skills\":\"")
                    .append(escapeJson(job.getSkills())).append("\"")
                    .append("}");
            }

            json.append("]");
            sendResponse(exchange, 200, json.toString());
        });

        // =====================================
        // REGISTER USER
        // =====================================
        server.createContext("/api/register", exchange -> {
            addCorsHeaders(exchange);

            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendResponse(exchange, 204, "");
                return;
            }

            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendResponse(exchange, 405,
                        "{\"message\":\"Method not allowed\"}");
                return;
            }

            String body = readBody(exchange);
            String name = getValue(body, "name");
            String email = getValue(body, "email");
            String password = getValue(body, "password");

            if (name.isBlank() || email.isBlank() || password.isBlank()) {
                sendResponse(exchange, 400,
                        "{\"message\":\"Please fill all fields\"}");
                return;
            }

            boolean registered =
                    userService.registerUser(name, email, password);

            if (registered) {
                sendResponse(exchange, 200,
                        "{\"message\":\"Registration successful\"}");
            } else {
                sendResponse(exchange, 409,
                        "{\"message\":\"Registration failed. Email may already be registered.\"}");
            }
        });

        // =====================================
        // LOGIN USER
        // =====================================
        server.createContext("/api/login", exchange -> {
            addCorsHeaders(exchange);

            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendResponse(exchange, 204, "");
                return;
            }

            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendResponse(exchange, 405,
                        "{\"message\":\"Method not allowed\"}");
                return;
            }

            String body = readBody(exchange);
            String email = getValue(body, "email");
            String password = getValue(body, "password");

            if (email.isBlank() || password.isBlank()) {
                sendResponse(exchange, 400,
                        "{\"message\":\"Enter email and password\"}");
                return;
            }

            int studentId = userService.loginUser(email, password);

            if (studentId > 0) {
                sendResponse(exchange, 200,
                        "{\"message\":\"Login successful\","
                        + "\"studentId\":" + studentId + "}");
            } else {
                sendResponse(exchange, 401,
                        "{\"message\":\"Invalid email or password\"}");
            }
        });

        // =====================================
        // APPLY FOR JOB
        // =====================================
        server.createContext("/api/apply", exchange -> {
            addCorsHeaders(exchange);

            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendResponse(exchange, 204, "");
                return;
            }

            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendResponse(exchange, 405,
                        "{\"message\":\"Method not allowed\"}");
                return;
            }

            String body = readBody(exchange);
            String studentIdText = getValue(body, "studentId");
            String jobIdText = getValue(body, "jobId");

            try {
                int studentId = Integer.parseInt(studentIdText);
                int jobId = Integer.parseInt(jobIdText);

                boolean applied =
                        applicationService.applyJob(studentId, jobId);

                if (applied) {
                    sendResponse(exchange, 200,
                            "{\"message\":\"Application submitted successfully\"}");
                } else {
                    sendResponse(exchange, 400,
                            "{\"message\":\"Application failed\"}");
                }

            } catch (NumberFormatException e) {
                sendResponse(exchange, 400,
                        "{\"message\":\"Invalid student ID or job ID\"}");
            }
        });

        // =====================================
        // GET APPLICATIONS FOR A STUDENT
        // GET /api/my-applications?studentId=5
        // =====================================
        server.createContext("/api/my-applications", exchange -> {
            addCorsHeaders(exchange);

            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendResponse(exchange, 204, "");
                return;
            }

            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendResponse(exchange, 405,
                        "{\"message\":\"Method not allowed\"}");
                return;
            }

            String query = exchange.getRequestURI().getQuery();
            String studentIdText = "";

            if (query != null) {
                for (String parameter : query.split("&")) {
                    String[] pair = parameter.split("=", 2);

                    if (pair.length == 2
                            && pair[0].equals("studentId")) {
                        studentIdText = pair[1];
                        break;
                    }
                }
            }

            try {
                int studentId = Integer.parseInt(studentIdText);

                if (studentId <= 0) {
                    throw new NumberFormatException();
                }

                List<Map<String, Object>> applications =
                        applicationService.getApplicationsByStudent(studentId);

                StringBuilder json = new StringBuilder("[");

                for (int i = 0; i < applications.size(); i++) {
                    Map<String, Object> app = applications.get(i);

                    if (i > 0) {
                        json.append(",");
                    }

                    json.append("{")
                        .append("\"applicationId\":")
                        .append(app.get("applicationId")).append(",")
                        .append("\"studentId\":")
                        .append(app.get("studentId")).append(",")
                        .append("\"jobId\":")
                        .append(app.get("jobId")).append(",")
                        .append("\"jobTitle\":\"")
                        .append(escapeJson(
                                String.valueOf(app.get("jobTitle"))))
                        .append("\",")
                        .append("\"company\":\"")
                        .append(escapeJson(
                                String.valueOf(app.get("company"))))
                        .append("\",")
                        .append("\"status\":\"")
                        .append(escapeJson(
                                String.valueOf(app.get("status"))))
                        .append("\",")
                        .append("\"appliedDate\":\"")
                        .append(escapeJson(
                                String.valueOf(app.get("appliedDate"))))
                        .append("\"}");
                }

                json.append("]");
                sendResponse(exchange, 200, json.toString());

            } catch (NumberFormatException e) {
                sendResponse(exchange, 400,
                        "{\"message\":\"A valid student ID is required\"}");
            }
        });

        server.start();

        System.out.println("JobPortal backend started!");
        System.out.println("Server running at http://localhost:8080");
    }

    // =====================================
    // CORS HEADERS
    // =====================================
    private static void addCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().set(
                "Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set(
                "Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().set(
                "Access-Control-Allow-Headers", "Content-Type");
        exchange.getResponseHeaders().set(
                "Content-Type", "application/json; charset=UTF-8");
    }

    // =====================================
    // READ REQUEST BODY
    // =====================================
    private static String readBody(HttpExchange exchange) {
        try (InputStream input = exchange.getRequestBody()) {
            return new String(
                    input.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(
                    "Unable to read request body", e);
        }
    }

    // =====================================
    // SEND HTTP RESPONSE
    // =====================================
    private static void sendResponse(
            HttpExchange exchange, int status, String response) {

        try {
            if (status == 204) {
                exchange.sendResponseHeaders(204, -1);
                exchange.close();
                return;
            }

            byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(status, bytes.length);

            try (OutputStream output = exchange.getResponseBody()) {
                output.write(bytes);
            }

        } catch (IOException e) {
            System.err.println(
                    "Error sending response: " + e.getMessage());
            exchange.close();
        }
    }

    // =====================================
    // GET A STRING VALUE FROM FRONTEND JSON
    // =====================================
    public static String getValue(String json, String key) {
        String search = "\"" + key + "\":\"";
        int start = json.indexOf(search);

        if (start == -1) {
            return "";
        }

        start += search.length();
        int end = json.indexOf("\"", start);

        if (end == -1) {
            return "";
        }

        return json.substring(start, end);
    }

    // =====================================
    // ESCAPE JSON STRING VALUES
    // =====================================
    private static String escapeJson(String value) {
        if (value == null) {
            return "";
        }

        return value.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");
    }
}