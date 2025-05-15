package com.quizapp.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Utility class for database operations.
 */
public class DatabaseUtil {
    private static final String DB_URL = "jdbc:sqlite:quiz_app.db";
    
    /**
     * Gets a connection to the database.
     *
     * @return a database connection
     * @throws SQLException if a connection cannot be established
     */
    public static Connection getConnection() throws SQLException {
        try {
            // Load the SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");
            
            // Create and return a connection
            return DriverManager.getConnection(DB_URL);
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQLite JDBC driver not found", e);
        }
    }
    
    /**
     * Closes the given database resources quietly (without throwing exceptions).
     */
    public static void closeQuietly(AutoCloseable... resources) {
        for (AutoCloseable resource : resources) {
            if (resource != null) {
                try {
                    resource.close();
                } catch (Exception e) {
                    // Log but suppress exceptions during close
                    System.err.println("Error closing resource: " + e.getMessage());
                }
            }
        }
    }
}