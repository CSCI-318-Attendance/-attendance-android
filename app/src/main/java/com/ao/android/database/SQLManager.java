package com.ao.android.database;

import com.ao.android.data.Student;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SQLManager {

    private static final String JDBC_DRIVER = "org.mariadb.jdbc.Driver";
    private static final String DB_URL = "jdbc:mariadb://azelabs.net/attendance_overview";
    private static final String DB_USERNAME = "azewilous";
    private static final String DB_PASSWORD = "Lucariza";

    private boolean isConnected = false;
    private Connection conn = null;

    private Student student;
    private UUID applicationId;

    public SQLManager(UUID applicationId) {
        try {
            this.applicationId = applicationId;
            Class.forName(JDBC_DRIVER);
            System.out.println("Registered jdbc driver.");
        } catch (ClassNotFoundException ex) {
            System.out.println("Could not locate jdbc driver: " + ex);
        }
    }

    public boolean connect() {
        System.out.println("Initiating connection to database.");
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            isConnected = true;
            System.out.println("You have successfully connected the database.");
        } catch (SQLException ex) {
            System.out.println("Could not create connection: " + ex);
        }
        return isConnected;
    }

    public boolean login(String user, String pass) {
        String query = "SELECT * FROM students";

        try {
            PreparedStatement prepStmt = conn.prepareStatement(query);
            ResultSet rs = prepStmt.executeQuery();
            while (rs.next()) {
                String store_user = rs.getString("username");
                String store_pass = rs.getString("password");
                if (user.equals(store_user) && pass.equals(store_pass)) {
                    student = new Student(rs.getString("student_id"));
                    student.setUsername(rs.getString("username"));
                    System.out.println("Found correct login");
                    conn.close();
                    return true;
                }
            }
            conn.close();
            System.out.println("Could not find login");
        } catch (SQLException ex) {
            System.out.println("There was an error when logging in: " + ex);
        }
        return false;
    }

    public boolean createUserAccount(String username, String email, String password,
                                     String studentId) {
        String appId = applicationId.toString();

        String query = "INSERT INTO students(username, password, student_id, email, device_unique_id)"
                + " VALUES(?, ?, ?, ?, ?)";
        try {
            PreparedStatement prepStmt = conn.prepareStatement(query);
            prepStmt.setString(1, username);
            prepStmt.setString(2, password);
            prepStmt.setString(3, studentId);
            prepStmt.setString(4, email);
            prepStmt.setString(5, appId);
            prepStmt.execute();
            conn.close();
            return true;
        } catch (SQLException ex) {
            System.out.println("There was an error with creating a user account: " + ex);
        }
        return false;
    }

    public List<UUID> checkAllDeviceIds() {
        List<UUID> applicationIds = new ArrayList<>();
        String query = "SELECT * FROM students";
        try {
            PreparedStatement prepStmt = conn.prepareStatement(query);
            ResultSet rs = prepStmt.executeQuery();
            while (rs.next()) {
                String device_id = rs.getString("device_unique_id");
                applicationIds.add(UUID.fromString(device_id));
            }
        } catch (SQLException ex) {
            System.out.println("There was an error when collecting device ids: " + ex);
        }
        return applicationIds;
    }

    public Student getStudent() {
        return student;
    }

}