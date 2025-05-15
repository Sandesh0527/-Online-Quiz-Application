package com.quizapp.dao;

import com.quizapp.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

/**
 * Initializes the database schema.
 */
public class DatabaseInitializer {
    
    private static final String CREATE_USERS_TABLE = 
            "CREATE TABLE IF NOT EXISTS users (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "username TEXT NOT NULL UNIQUE," +
            "password TEXT NOT NULL," + // Hashed password
            "email TEXT NOT NULL UNIQUE," +
            "is_admin INTEGER NOT NULL DEFAULT 0" + // Boolean: 0 = false, 1 = true
            ");";
    
    private static final String CREATE_QUIZZES_TABLE = 
            "CREATE TABLE IF NOT EXISTS quizzes (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "title TEXT NOT NULL," +
            "description TEXT," +
            "creator_id INTEGER NOT NULL," +
            "time_limit INTEGER DEFAULT 0," + // Time limit in minutes, 0 = no limit
            "created_at TEXT DEFAULT CURRENT_TIMESTAMP," +
            "FOREIGN KEY (creator_id) REFERENCES users(id)" +
            ");";
    
    private static final String CREATE_QUESTIONS_TABLE = 
            "CREATE TABLE IF NOT EXISTS questions (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "quiz_id INTEGER NOT NULL," +
            "text TEXT NOT NULL," +
            "points INTEGER DEFAULT 1," +
            "FOREIGN KEY (quiz_id) REFERENCES quizzes(id) ON DELETE CASCADE" +
            ");";
    
    private static final String CREATE_OPTIONS_TABLE = 
            "CREATE TABLE IF NOT EXISTS options (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "question_id INTEGER NOT NULL," +
            "text TEXT NOT NULL," +
            "is_correct INTEGER NOT NULL DEFAULT 0," + // Boolean: 0 = false, 1 = true
            "FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE" +
            ");";
    
    private static final String CREATE_QUIZ_RESULTS_TABLE = 
            "CREATE TABLE IF NOT EXISTS quiz_results (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "user_id INTEGER NOT NULL," +
            "quiz_id INTEGER NOT NULL," +
            "score INTEGER NOT NULL," +
            "max_score INTEGER NOT NULL," +
            "duration_seconds INTEGER," + // Time taken to complete the quiz in seconds
            "completed_at TEXT DEFAULT CURRENT_TIMESTAMP," +
            "FOREIGN KEY (user_id) REFERENCES users(id)," +
            "FOREIGN KEY (quiz_id) REFERENCES quizzes(id)" +
            ");";
    
    private static final String CREATE_QUESTION_RESULTS_TABLE = 
            "CREATE TABLE IF NOT EXISTS question_results (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "quiz_result_id INTEGER NOT NULL," +
            "question_id INTEGER NOT NULL," +
            "is_correct INTEGER NOT NULL DEFAULT 0," + // Boolean: 0 = false, 1 = true
            "FOREIGN KEY (quiz_result_id) REFERENCES quiz_results(id) ON DELETE CASCADE," +
            "FOREIGN KEY (question_id) REFERENCES questions(id)" +
            ");";
    
    private static final String CREATE_SELECTED_OPTIONS_TABLE = 
            "CREATE TABLE IF NOT EXISTS selected_options (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "question_result_id INTEGER NOT NULL," +
            "option_id INTEGER NOT NULL," +
            "FOREIGN KEY (question_result_id) REFERENCES question_results(id) ON DELETE CASCADE," +
            "FOREIGN KEY (option_id) REFERENCES options(id)" +
            ");";
    
    private static final String CREATE_ADMIN_USER =
            "INSERT OR IGNORE INTO users (username, password, email, is_admin) " +
            "VALUES ('admin', '$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqaFS', " +
            "'admin@example.com', 1);"; // Default password: 'admin123'
    
    /**
     * Initializes the database schema by creating all necessary tables.
     * @throws SQLException if a database error occurs
     */
    public static void initializeDatabase() throws SQLException {
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Create tables
            stmt.executeUpdate(CREATE_USERS_TABLE);
            stmt.executeUpdate(CREATE_QUIZZES_TABLE);
            stmt.executeUpdate(CREATE_QUESTIONS_TABLE);
            stmt.executeUpdate(CREATE_OPTIONS_TABLE);
            stmt.executeUpdate(CREATE_QUIZ_RESULTS_TABLE);
            stmt.executeUpdate(CREATE_QUESTION_RESULTS_TABLE);
            stmt.executeUpdate(CREATE_SELECTED_OPTIONS_TABLE);
            
            // Create admin user
            stmt.executeUpdate(CREATE_ADMIN_USER);
            
            System.out.println("Database initialized successfully.");
        }
    }
}