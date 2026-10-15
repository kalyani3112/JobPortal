
package com.jobportal.service;

import com.jobportal.DBConnection;
import com.jobportal.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class UserService {

    // Register a new student
    public boolean registerUser(String name, String email, String password) {

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

                checkStatement.setString(1, email);

                try (ResultSet result = checkStatement.executeQuery()) {
                    if (result.next()) {
                        return false;
                    }
                }
            }

            // Insert the new student
            try (PreparedStatement insertStatement =
                         connection.prepareStatement(insertSql)) {

                insertStatement.setString(1, name);
                insertStatement.setString(2, email);
                insertStatement.setString(3, password);

                return insertStatement.executeUpdate() > 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Return the student ID when login succeeds; return -1 otherwise
    public int loginUser(String email, String password) {

        String sql =
                "SELECT student_id FROM students WHERE email = ? AND password = ?";

        try (Connection connection = DBConnection.getConnection()) {

            if (connection == null) {
                return -1;
            }

            try (PreparedStatement statement =
                         connection.prepareStatement(sql)) {

                statement.setString(1, email);
                statement.setString(2, password);

                try (ResultSet result = statement.executeQuery()) {
                    if (result.next()) {
                        return result.getInt("student_id");
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