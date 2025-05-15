package com.quizapp.dao;

import com.quizapp.model.QuizResult;
import com.quizapp.model.QuizResult.QuestionResult;
import com.quizapp.util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for QuizResult operations.
 */
public class QuizResultDAO {
    
    /**
     * Saves a quiz result to the database.
     *
     * @param result the quiz result to save
     * @return the saved quiz result with ID set
     * @throws SQLException if a database error occurs
     */
    public QuizResult saveQuizResult(QuizResult result) throws SQLException {
        String sqlResult = "INSERT INTO quiz_results (user_id, quiz_id, score, max_score, duration_seconds) " +
                          "VALUES (?, ?, ?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement stmtResult = null;
        ResultSet rsResult = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false); // Start transaction
            
            // Insert quiz result
            stmtResult = conn.prepareStatement(sqlResult, Statement.RETURN_GENERATED_KEYS);
            stmtResult.setInt(1, result.getUserId());
            stmtResult.setInt(2, result.getQuizId());
            stmtResult.setInt(3, result.getScore());
            stmtResult.setInt(4, result.getMaxScore());
            stmtResult.setLong(5, result.getDurationInSeconds());
            
            int affectedRows = stmtResult.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Saving quiz result failed, no rows affected.");
            }
            
            rsResult = stmtResult.getGeneratedKeys();
            if (rsResult.next()) {
                int resultId = rsResult.getInt(1);
                result.setId(resultId);
                
                // Save question results
                for (QuestionResult questionResult : result.getQuestionResults()) {
                    questionResult.setQuizResultId(resultId);
                    saveQuestionResult(conn, questionResult);
                }
                
                conn.commit(); // Commit transaction
            } else {
                conn.rollback(); // Rollback if we didn't get an ID
                throw new SQLException("Saving quiz result failed, no ID obtained.");
            }
            
            return result;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback on error
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Reset auto-commit
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            DatabaseUtil.closeQuietly(rsResult, stmtResult, conn);
        }
    }
    
    /**
     * Saves a question result to the database.
     *
     * @param conn the database connection
     * @param questionResult the question result to save
     * @return the saved question result with ID set
     * @throws SQLException if a database error occurs
     */
    private QuestionResult saveQuestionResult(Connection conn, QuestionResult questionResult) throws SQLException {
        String sqlQuestionResult = "INSERT INTO question_results (quiz_result_id, question_id, is_correct) " +
                                  "VALUES (?, ?, ?)";
        
        PreparedStatement stmtQuestionResult = null;
        ResultSet rsQuestionResult = null;
        
        try {
            // Insert question result
            stmtQuestionResult = conn.prepareStatement(sqlQuestionResult, Statement.RETURN_GENERATED_KEYS);
            stmtQuestionResult.setInt(1, questionResult.getQuizResultId());
            stmtQuestionResult.setInt(2, questionResult.getQuestionId());
            stmtQuestionResult.setInt(3, questionResult.isCorrect() ? 1 : 0);
            
            int affectedRows = stmtQuestionResult.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Saving question result failed, no rows affected.");
            }
            
            rsQuestionResult = stmtQuestionResult.getGeneratedKeys();
            if (rsQuestionResult.next()) {
                int questionResultId = rsQuestionResult.getInt(1);
                questionResult.setId(questionResultId);
                
                // Save selected options
                if (questionResult.getSelectedOptionIds() != null && !questionResult.getSelectedOptionIds().isEmpty()) {
                    for (Integer optionId : questionResult.getSelectedOptionIds()) {
                        saveSelectedOption(conn, questionResultId, optionId);
                    }
                }
            } else {
                throw new SQLException("Saving question result failed, no ID obtained.");
            }
            
            return questionResult;
        } finally {
            DatabaseUtil.closeQuietly(rsQuestionResult, stmtQuestionResult);
        }
    }
    
    /**
     * Saves a selected option to the database.
     *
     * @param conn the database connection
     * @param questionResultId the ID of the question result
     * @param optionId the ID of the selected option
     * @throws SQLException if a database error occurs
     */
    private void saveSelectedOption(Connection conn, int questionResultId, int optionId) throws SQLException {
        String sqlSelectedOption = "INSERT INTO selected_options (question_result_id, option_id) VALUES (?, ?)";
        
        PreparedStatement stmtSelectedOption = null;
        
        try {
            stmtSelectedOption = conn.prepareStatement(sqlSelectedOption);
            stmtSelectedOption.setInt(1, questionResultId);
            stmtSelectedOption.setInt(2, optionId);
            
            stmtSelectedOption.executeUpdate();
        } finally {
            DatabaseUtil.closeQuietly(stmtSelectedOption);
        }
    }
    
    /**
     * Gets all quiz results for a user.
     *
     * @param userId the ID of the user
     * @return a list of quiz results
     * @throws SQLException if a database error occurs
     */
    public List<QuizResult> getQuizResultsByUser(int userId) throws SQLException {
        String sql = "SELECT qr.id, qr.user_id, qr.quiz_id, q.title AS quiz_title, qr.score, qr.max_score, " +
                     "qr.duration_seconds, qr.completed_at FROM quiz_results qr " +
                     "JOIN quizzes q ON qr.quiz_id = q.id WHERE qr.user_id = ? ORDER BY qr.completed_at DESC";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            
            rs = stmt.executeQuery();
            
            List<QuizResult> results = new ArrayList<>();
            
            while (rs.next()) {
                QuizResult result = new QuizResult(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getInt("quiz_id"),
                    rs.getString("quiz_title"),
                    rs.getInt("score"),
                    rs.getInt("max_score"),
                    rs.getTimestamp("completed_at").toLocalDateTime(),
                    rs.getLong("duration_seconds")
                );
                
                results.add(result);
            }
            
            return results;
        } finally {
            DatabaseUtil.closeQuietly(rs, stmt, conn);
        }
    }
    
    /**
     * Gets all quiz results for a quiz.
     *
     * @param quizId the ID of the quiz
     * @return a list of quiz results
     * @throws SQLException if a database error occurs
     */
    public List<QuizResult> getQuizResultsByQuiz(int quizId) throws SQLException {
        String sql = "SELECT qr.id, qr.user_id, qr.quiz_id, q.title AS quiz_title, qr.score, qr.max_score, " +
                     "qr.duration_seconds, qr.completed_at, u.username FROM quiz_results qr " +
                     "JOIN quizzes q ON qr.quiz_id = q.id " + 
                     "JOIN users u ON qr.user_id = u.id " +
                     "WHERE qr.quiz_id = ? ORDER BY qr.score DESC, qr.duration_seconds ASC";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, quizId);
            
            rs = stmt.executeQuery();
            
            List<QuizResult> results = new ArrayList<>();
            
            while (rs.next()) {
                QuizResult result = new QuizResult(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getInt("quiz_id"),
                    rs.getString("quiz_title"),
                    rs.getInt("score"),
                    rs.getInt("max_score"),
                    rs.getTimestamp("completed_at").toLocalDateTime(),
                    rs.getLong("duration_seconds")
                );
                
                results.add(result);
            }
            
            return results;
        } finally {
            DatabaseUtil.closeQuietly(rs, stmt, conn);
        }
    }
    
    /**
     * Gets a quiz result by ID with all its question results.
     *
     * @param resultId the ID of the quiz result
     * @return the quiz result, or null if not found
     * @throws SQLException if a database error occurs
     */
    public QuizResult getQuizResultById(int resultId) throws SQLException {
        String sql = "SELECT qr.id, qr.user_id, qr.quiz_id, q.title AS quiz_title, qr.score, qr.max_score, " +
                     "qr.duration_seconds, qr.completed_at FROM quiz_results qr " +
                     "JOIN quizzes q ON qr.quiz_id = q.id WHERE qr.id = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, resultId);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                QuizResult result = new QuizResult(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getInt("quiz_id"),
                    rs.getString("quiz_title"),
                    rs.getInt("score"),
                    rs.getInt("max_score"),
                    rs.getTimestamp("completed_at").toLocalDateTime(),
                    rs.getLong("duration_seconds")
                );
                
                // Get question results for this quiz result
                List<QuestionResult> questionResults = getQuestionResults(conn, resultId);
                result.setQuestionResults(questionResults);
                
                return result;
            } else {
                return null;
            }
        } finally {
            DatabaseUtil.closeQuietly(rs, stmt, conn);
        }
    }
    
    /**
     * Gets all question results for a quiz result.
     *
     * @param conn the database connection
     * @param quizResultId the ID of the quiz result
     * @return a list of question results
     * @throws SQLException if a database error occurs
     */
    private List<QuestionResult> getQuestionResults(Connection conn, int quizResultId) throws SQLException {
        String sql = "SELECT qr.id, qr.quiz_result_id, qr.question_id, q.text AS question_text, " +
                     "qr.is_correct, q.points FROM question_results qr " +
                     "JOIN questions q ON qr.question_id = q.id WHERE qr.quiz_result_id = ?";
        
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, quizResultId);
            
            rs = stmt.executeQuery();
            
            List<QuestionResult> questionResults = new ArrayList<>();
            
            while (rs.next()) {
                int questionResultId = rs.getInt("id");
                
                // Get selected options for this question result
                List<Integer> selectedOptionIds = getSelectedOptions(conn, questionResultId);
                
                QuestionResult questionResult = new QuestionResult(
                    questionResultId,
                    rs.getInt("quiz_result_id"),
                    rs.getInt("question_id"),
                    rs.getString("question_text"),
                    rs.getInt("is_correct") == 1,
                    selectedOptionIds,
                    rs.getInt("points")
                );
                
                questionResults.add(questionResult);
            }
            
            return questionResults;
        } finally {
            DatabaseUtil.closeQuietly(rs, stmt);
        }
    }
    
    /**
     * Gets all selected option IDs for a question result.
     *
     * @param conn the database connection
     * @param questionResultId the ID of the question result
     * @return a list of selected option IDs
     * @throws SQLException if a database error occurs
     */
    private List<Integer> getSelectedOptions(Connection conn, int questionResultId) throws SQLException {
        String sql = "SELECT option_id FROM selected_options WHERE question_result_id = ?";
        
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, questionResultId);
            
            rs = stmt.executeQuery();
            
            List<Integer> selectedOptionIds = new ArrayList<>();
            
            while (rs.next()) {
                selectedOptionIds.add(rs.getInt("option_id"));
            }
            
            return selectedOptionIds;
        } finally {
            DatabaseUtil.closeQuietly(rs, stmt);
        }
    }
}