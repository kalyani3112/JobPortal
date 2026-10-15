
package com.jobportal.service;

import com.jobportal.model.Job;
import com.jobportal.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class JobService {

    public List<Job> getAllJobs() {

        List<Job> jobs = new ArrayList<>();

        String sql =
            "SELECT j.job_id, j.job_role, c.company_name, " +
            "c.location, 'Java, SQL' AS skills " +
            "FROM jobs j " +
            "JOIN companies c ON j.company_id = c.company_id";

        try (Connection connection = DBConnection.getConnection()) {

            if (connection == null) {
                System.out.println("Database connection failed.");
                return jobs;
            }

            try (PreparedStatement statement =
                     connection.prepareStatement(sql);
                 ResultSet result = statement.executeQuery()) {

                while (result.next()) {

                    Job job = new Job();

                    job.setId(result.getInt("job_id"));
                    job.setTitle(result.getString("job_role"));
                    job.setCompany(result.getString("company_name"));
                    job.setLocation(result.getString("location"));
                    job.setSkills(result.getString("skills"));

                    jobs.add(job);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return jobs;
    }

    public Job getJobById(int id) {

        List<Job> jobs = getAllJobs();

        for (Job job : jobs) {
            if (job.getId() == id) {
                return job;
            }
        }

        return null;
    }
}