
package com.jobportal.service;

import com.jobportal.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApplicationService {

    // Apply for a job
    public boolean applyJob(int studentId, int jobId) {

        String sql =
                "INSERT INTO applications " +
                "(student_id, job_id, status, applied_date) " +
                "VALUES (?, ?, 'Applied', CURDATE())";

        try (Connection connection = DBConnection.getConnection()) {

            if (connection == null) {
                System.err.println("Database connection failed.");
                return false;
            }

            try (PreparedStatement statement =
                         connection.prepareStatement(sql)) {

                statement.setInt(1, studentId);
                statement.setInt(2, jobId);

                return statement.executeUpdate() > 0;
            }

        } catch (Exception e) {
            System.err.println("Error applying for job.");
            e.printStackTrace();
            return false;
        }
    }

    // Get applications for a student
    public List<Map<String, Object>> getApplicationsByStudent(
            int studentId) {

        List<Map<String, Object>> applications = new ArrayList<>();

        String sql =
                "SELECT a.application_id, a.student_id, a.job_id, " +
                "a.status, a.applied_date, " +
                "j.job_role AS title, " +
                "c.company_name AS company " +
                "FROM applications a " +
                "JOIN jobs j ON a.job_id = j.job_id " +
                "JOIN companies c ON j.company_id = c.company_id " +
                "WHERE a.student_id = ? " +
                "ORDER BY a.application_id DESC";

        try (Connection connection = DBConnection.getConnection()) {

            if (connection == null) {
                System.err.println("Database connection failed.");
                return applications;
            }

            try (PreparedStatement statement =
                         connection.prepareStatement(sql)) {

                statement.setInt(1, studentId);

                try (ResultSet result = statement.executeQuery()) {

                    while (result.next()) {

                        Map<String, Object> application = new HashMap<>();

                        application.put(
                                "applicationId",
                                result.getInt("application_id"));

                        application.put(
                                "studentId",
                                result.getInt("student_id"));

                        application.put(
                                "jobId",
                                result.getInt("job_id"));

                        application.put(
                                "jobTitle",
                                result.getString("title"));

                        application.put(
                                "company",
                                result.getString("company"));

                        application.put(
                                "status",
                                result.getString("status"));

                        application.put(
                                "appliedDate",
                                result.getString("applied_date"));

                        applications.add(application);
                    }
                }
            }

        } catch (Exception e) {
            System.err.println(
                    "Error retrieving applications for student "
                    + studentId);
            e.printStackTrace();
        }

        return applications;
    }
}
