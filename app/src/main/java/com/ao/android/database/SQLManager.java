package com.ao.android.database;

import com.ao.android.data.Student;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLManager {

    private static final String JDBC_DRIVER = "org.mariadb.jdbc.Driver";
    private static final String DB_URL = "jdbc:mariadb://azelabs.net/attendance_overview";
    private static final String DB_USERNAME = "azewilous";
    private static final String DB_PASSWORD = "Lucariza";

    private boolean isConnected = false;
    private Connection conn = null;

    private Student student;

    public SQLManager() {
        try {
            Class.forName(JDBC_DRIVER);
            System.out.println("Registered jdbc driver.");
        } catch (ClassNotFoundException ex) {
            System.out.println("Could not locate jdbc driver: " + ex);
            return;
        }
        System.out.println("Initiating connection to database.");
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            isConnected = true;
            System.out.println("You have successfully connected the database.");
        } catch (SQLException ex) {
            System.out.println("Could not create connection: " + ex);
        }
    }

    public boolean isInitialize() {
        return isConnected && conn != null;
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
                    return true;
                }
            }
            System.out.println("Could not find login");
        } catch (SQLException ex) {
            System.out.println("There was an error when logging in: " + ex);
        }
        return false;
    }

    public Student getStudent() {
        return student;
    }

}