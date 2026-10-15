
package com.jobportal.service;

import com.jobportal.DBConnection;
import com.jobportal.PasswordUtil;
import com.jobportal.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class UserService {

    // Register a new student with a hashed password
    public boolean registerUser(String name, String email, String password) {

        if (name == null || email == null || password == null
                || name.isBlank() || email.isBlank() || password.isEmpty()) {
            return false;
        }

        String checkSql =
                "SELECT student_id FROM students WHERE email = ?";

        String insertSql =
                "INSERT INTO students (name, email, password) VALUES (?, ?, ?)";

        try (Connection connection = DBConnection.getConnection()) {

            if (connection == null) {
                return false;
            }

            // Check whether the email already exists
            try (PreparedStatement checkStatement =
                         connection.prepareStatement(checkSql)) {

                checkStatement.setString(1, email.trim());

                try (ResultSet result = checkStatement.executeQuery()) {
                    if (result.next()) {
                        return false;
                    }
                }
            }

            // Hash the password before saving it
            String hashedPassword = PasswordUtil.hashPassword(password);

            try (PreparedStatement insertStatement =
                         connection.prepareStatement(insertSql)) {

                insertStatement.setString(1, name.trim());
                insertStatement.setString(2, email.trim());
                insertStatement.setString(3, hashedPassword);

                return insertStatement.executeUpdate() > 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Return the student ID when login succeeds; otherwise return -1
    public int loginUser(String email, String password) {

        if (email == null || password == null
                || email.isBlank() || password.isEmpty()) {
            return -1;
        }

        String sql =
                "SELECT student_id, password FROM students WHERE email = ?";

        try (Connection connection = DBConnection.getConnection()) {

            if (connection == null) {
                return -1;
            }

            try (PreparedStatement statement =
                         connection.prepareStatement(sql)) {

                statement.setString(1, email.trim());

                try (ResultSet result = statement.executeQuery()) {
                    if (result.next()) {

                        int studentId = result.getInt("student_id");
                        String storedPassword = result.getString("password");

                        // Verify the submitted password against the stored hash
                        if (PasswordUtil.verifyPassword(
                                password, storedPassword)) {
                            return studentId;
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    // Get users without retrieving their passwords
    public List<User> getAllUsers() {

        List<User> users = new ArrayList<>();

        String sql = "SELECT name, email FROM students";

        try (Connection connection = DBConnection.getConnection()) {

            if (connection == null) {
                return users;
            }

            try (PreparedStatement statement =
                         connection.prepareStatement(sql);
                 ResultSet result = statement.executeQuery()) {

                while (result.next()) {
                    User user = new User(
                            result.getString("name"),
                            result.getString("email"),
                            ""
                    );

                    users.add(user);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return users;
    }
}
