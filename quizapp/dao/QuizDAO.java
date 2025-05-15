package com.quizapp.dao;

import com.quizapp.model.Quiz;
import com.quizapp.model.Question;
import com.quizapp.model.Question.Option;
import com.quizapp.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Quiz operations.
 */
public class QuizDAO {
    
    /**
     * Creates a new quiz in the database.
     *
     * @param quiz the quiz to create
     * @return the created quiz with ID set
     * @throws SQLException if a database error occurs
     */
    public Quiz createQuiz(Quiz quiz) throws SQLException {
        String sql = "INSERT INTO quizzes (title, description, creator_id, time_limit) VALUES (?, ?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false); // Start transaction
            
            // Insert quiz
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, quiz.getTitle());
            stmt.setString(2, quiz.getDescription());
            stmt.setInt(3, quiz.getCreatorId());
            stmt.setInt(4, quiz.getTimeLimit());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating quiz failed, no rows affected.");
            }
            
            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int quizId = rs.getInt(1);
                quiz.setId(quizId);
                
                // If there are questions, insert them too
                if (!quiz.getQuestions().isEmpty()) {
                    for (Question question : quiz.getQuestions()) {
                        question.setQuizId(quizId);
                        createQuestion(conn, question);
                    }
                }
                
                conn.commit(); // Commit transaction
            } else {
                conn.rollback(); // Rollback if we didn't get an ID
                throw new SQLException("Creating quiz failed, no ID obtained.");
            }
            
            return quiz;
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
            DatabaseUtil.closeQuietly(rs, stmt, conn);
        }
    }
    
    /**
     * Creates a new question for a quiz.
     *
     * @param conn the database connection
     * @param question the question to create
     * @return the created question with ID set
     * @throws SQLException if a database error occurs
     */
    private Question createQuestion(Connection conn, Question question) throws SQLException {
        String sql = "INSERT INTO questions (quiz_id, text, points) VALUES (?, ?, ?)";
        
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            // Insert question
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, question.getQuizId());
            stmt.setString(2, question.getText());
            stmt.setInt(3, question.getPoints());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating question failed, no rows affected.");
            }
            
            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int questionId = rs.getInt(1);
                question.setId(questionId);
                
                // If there are options, insert them too
                if (!question.getOptions().isEmpty()) {
                    for (Option option : question.getOptions()) {
                        option.setQuestionId(questionId);
                        createOption(conn, option);
                    }
                }
            } else {
                throw new SQLException("Creating question failed, no ID obtained.");
            }
            
            return question;
        } finally {
            DatabaseUtil.closeQuietly(rs, stmt);
        }
    }
    
    /**
     * Creates a new option for a question.
     *
     * @param conn the database connection
     * @param option the option to create
     * @return the created option with ID set
     * @throws SQLException if a database error occurs
     */
    private Option createOption(Connection conn, Option option) throws SQLException {
        String sql = "INSERT INTO options (question_id, text, is_correct) VALUES (?, ?, ?)";
        
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            // Insert option
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, option.getQuestionId());
            stmt.setString(2, option.getText());
            stmt.setInt(3, option.isCorrect() ? 1 : 0);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating option failed, no rows affected.");
            }
            
            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                option.setId(rs.getInt(1));
            } else {
                throw new SQLException("Creating option failed, no ID obtained.");
            }
            
            return option;
        } finally {
            DatabaseUtil.closeQuietly(rs, stmt);
        }
    }
    
    /**
     * Gets a quiz by ID with all its questions and options.
     *
     * @param quizId the ID of the quiz to get
     * @return the quiz, or null if not found
     * @throws SQLException if a database error occurs
     */
    public Quiz getQuizById(int quizId) throws SQLException {
        String sqlQuiz = "SELECT q.id, q.title, q.description, q.creator_id, u.username AS creator_name, " +
                         "q.time_limit FROM quizzes q JOIN users u ON q.creator_id = u.id WHERE q.id = ?";
        
        Connection conn = null;
        PreparedStatement stmtQuiz = null;
        ResultSet rsQuiz = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmtQuiz = conn.prepareStatement(sqlQuiz);
            stmtQuiz.setInt(1, quizId);
            
            rsQuiz = stmtQuiz.executeQuery();
            
            if (rsQuiz.next()) {
                Quiz quiz = new Quiz(
                    rsQuiz.getInt("id"),
                    rsQuiz.getString("title"),
                    rsQuiz.getString("description"),
                    rsQuiz.getInt("creator_id"),
                    rsQuiz.getString("creator_name"),
                    rsQuiz.getInt("time_limit")
                );
                
                // Get questions for this quiz
                List<Question> questions = getQuestionsForQuiz(conn, quizId);
                quiz.setQuestions(questions);
                
                return quiz;
            } else {
                return null;
            }
        } finally {
            DatabaseUtil.closeQuietly(rsQuiz, stmtQuiz, conn);
        }
    }
    
    /**
     * Gets all questions for a quiz.
     *
     * @param conn the database connection
     * @param quizId the ID of the quiz
     * @return a list of questions
     * @throws SQLException if a database error occurs
     */
    private List<Question> getQuestionsForQuiz(Connection conn, int quizId) throws SQLException {
        String sqlQuestions = "SELECT id, quiz_id, text, points FROM questions WHERE quiz_id = ? ORDER BY id";
        
        PreparedStatement stmtQuestions = null;
        ResultSet rsQuestions = null;
        
        try {
            stmtQuestions = conn.prepareStatement(sqlQuestions);
            stmtQuestions.setInt(1, quizId);
            
            rsQuestions = stmtQuestions.executeQuery();
            
            List<Question> questions = new ArrayList<>();
            
            while (rsQuestions.next()) {
                Question question = new Question(
                    rsQuestions.getInt("id"),
                    rsQuestions.getInt("quiz_id"),
                    rsQuestions.getString("text"),
                    rsQuestions.getInt("points")
                );
                
                // Get options for this question
                List<Option> options = getOptionsForQuestion(conn, question.getId());
                question.setOptions(options);
                
                questions.add(question);
            }
            
            return questions;
        } finally {
            DatabaseUtil.closeQuietly(rsQuestions, stmtQuestions);
        }
    }
    
    /**
     * Gets all options for a question.
     *
     * @param conn the database connection
     * @param questionId the ID of the question
     * @return a list of options
     * @throws SQLException if a database error occurs
     */
    private List<Option> getOptionsForQuestion(Connection conn, int questionId) throws SQLException {
        String sqlOptions = "SELECT id, question_id, text, is_correct FROM options WHERE question_id = ? ORDER BY id";
        
        PreparedStatement stmtOptions = null;
        ResultSet rsOptions = null;
        
        try {
            stmtOptions = conn.prepareStatement(sqlOptions);
            stmtOptions.setInt(1, questionId);
            
            rsOptions = stmtOptions.executeQuery();
            
            List<Option> options = new ArrayList<>();
            
            while (rsOptions.next()) {
                Option option = new Option(
                    rsOptions.getInt("id"),
                    rsOptions.getInt("question_id"),
                    rsOptions.getString("text"),
                    rsOptions.getInt("is_correct") == 1
                );
                
                options.add(option);
            }
            
            return options;
        } finally {
            DatabaseUtil.closeQuietly(rsOptions, stmtOptions);
        }
    }
    
    /**
     * Gets all quizzes.
     *
     * @return a list of all quizzes (without questions)
     * @throws SQLException if a database error occurs
     */
    public List<Quiz> getAllQuizzes() throws SQLException {
        String sql = "SELECT q.id, q.title, q.description, q.creator_id, u.username AS creator_name, " +
                     "q.time_limit FROM quizzes q JOIN users u ON q.creator_id = u.id ORDER BY q.id";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            
            rs = stmt.executeQuery();
            
            List<Quiz> quizzes = new ArrayList<>();
            
            while (rs.next()) {
                Quiz quiz = new Quiz(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getInt("creator_id"),
                    rs.getString("creator_name"),
                    rs.getInt("time_limit")
                );
                
                quizzes.add(quiz);
            }
            
            return quizzes;
        } finally {
            DatabaseUtil.closeQuietly(rs, stmt, conn);
        }
    }
    
    /**
     * Gets all quizzes created by a user.
     *
     * @param creatorId the ID of the creator
     * @return a list of quizzes created by the user
     * @throws SQLException if a database error occurs
     */
    public List<Quiz> getQuizzesByCreator(int creatorId) throws SQLException {
        String sql = "SELECT q.id, q.title, q.description, q.creator_id, u.username AS creator_name, " +
                     "q.time_limit FROM quizzes q JOIN users u ON q.creator_id = u.id " +
                     "WHERE q.creator_id = ? ORDER BY q.id";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, creatorId);
            
            rs = stmt.executeQuery();
            
            List<Quiz> quizzes = new ArrayList<>();
            
            while (rs.next()) {
                Quiz quiz = new Quiz(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getInt("creator_id"),
                    rs.getString("creator_name"),
                    rs.getInt("time_limit")
                );
                
                quizzes.add(quiz);
            }
            
            return quizzes;
        } finally {
            DatabaseUtil.closeQuietly(rs, stmt, conn);
        }
    }
    
    /**
     * Updates a quiz in the database.
     *
     * @param quiz the quiz to update
     * @return true if successful, false otherwise
     * @throws SQLException if a database error occurs
     */
    public boolean updateQuiz(Quiz quiz) throws SQLException {
        String sql = "UPDATE quizzes SET title = ?, description = ?, time_limit = ? WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            
            stmt.setString(1, quiz.getTitle());
            stmt.setString(2, quiz.getDescription());
            stmt.setInt(3, quiz.getTimeLimit());
            stmt.setInt(4, quiz.getId());
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } finally {
            DatabaseUtil.closeQuietly(stmt, conn);
        }
    }
    
    /**
     * Deletes a quiz from the database.
     *
     * @param quizId the ID of the quiz to delete
     * @return true if successful, false otherwise
     * @throws SQLException if a database error occurs
     */
    public boolean deleteQuiz(int quizId) throws SQLException {
        String sql = "DELETE FROM quizzes WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, quizId);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } finally {
            DatabaseUtil.closeQuietly(stmt, conn);
        }
    }
    
    /**
     * Deletes a question from the database.
     *
     * @param questionId the ID of the question to delete
     * @return true if successful, false otherwise
     * @throws SQLException if a database error occurs
     */
    public boolean deleteQuestion(int questionId) throws SQLException {
        String sql = "DELETE FROM questions WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, questionId);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } finally {
            DatabaseUtil.closeQuietly(stmt, conn);
        }
    }
    
    /**
     * Updates a question in the database.
     *
     * @param question the question to update
     * @return true if successful, false otherwise
     * @throws SQLException if a database error occurs
     */
    public boolean updateQuestion(Question question) throws SQLException {
        String sql = "UPDATE questions SET text = ?, points = ? WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            
            stmt.setString(1, question.getText());
            stmt.setInt(2, question.getPoints());
            stmt.setInt(3, question.getId());
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } finally {
            DatabaseUtil.closeQuietly(stmt, conn);
        }
    }
    
    /**
     * Adds a question to a quiz.
     *
     * @param quizId the ID of the quiz
     * @param question the question to add
     * @return the created question with ID set
     * @throws SQLException if a database error occurs
     */
    public Question addQuestionToQuiz(int quizId, Question question) throws SQLException {
        String sql = "INSERT INTO questions (quiz_id, text, points) VALUES (?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false); // Start transaction
            
            // Insert question
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, quizId);
            stmt.setString(2, question.getText());
            stmt.setInt(3, question.getPoints());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating question failed, no rows affected.");
            }
            
            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int questionId = rs.getInt(1);
                question.setId(questionId);
                question.setQuizId(quizId);
                
                // If there are options, insert them too
                if (!question.getOptions().isEmpty()) {
                    for (Option option : question.getOptions()) {
                        option.setQuestionId(questionId);
                        createOption(conn, option);
                    }
                }
                
                conn.commit(); // Commit transaction
            } else {
                conn.rollback(); // Rollback if we didn't get an ID
                throw new SQLException("Creating question failed, no ID obtained.");
            }
            
            return question;
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
            DatabaseUtil.closeQuietly(rs, stmt, conn);
        }
    }
}