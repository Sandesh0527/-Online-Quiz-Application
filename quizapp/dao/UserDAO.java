package com.quizapp.dao;

import com.quizapp.model.User;
import com.quizapp.util.DatabaseUtil;
import com.quizapp.util.PasswordUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for User operations.
 */
public class UserDAO {
    
    /**
     * Creates a new user in the database.
     *
     * @param user the user to create
     * @return the created user with ID set
     * @throws SQLException if a database error occurs
     */
    public User createUser(User user) throws SQLException {
        String sql = "INSERT INTO users (username, password, email, is_admin) VALUES (?, ?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            stmt.setString(1, user.getUsername());
            stmt.setString(2, PasswordUtil.hashPassword(user.getPassword()));
            stmt.setString(3, user.getEmail());
            stmt.setInt(4, user.isAdmin() ? 1 : 0);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }
            
            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                user.setId(rs.getInt(1));
            } else {
                throw new SQLException("Creating user failed, no ID obtained.");
            }
            
            return user;
        } finally {
            DatabaseUtil.closeQuietly(rs, stmt, conn);
        }
    }
    
    /**
     * Gets a user by ID.
     *
     * @param id the ID of the user to get
     * @return the user, or null if not found
     * @throws SQLException if a database error occurs
     */
    public User getUserById(int id) throws SQLException {
        String sql = "SELECT id, username, password, email, is_admin FROM users WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new User(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("email"),
                    rs.getInt("is_admin") == 1
                );
            } else {
                return null;
            }
        } finally {
            DatabaseUtil.closeQuietly(rs, stmt, conn);
        }
    }
    
    /**
     * Gets a user by username.
     *
     * @param username the username of the user to get
     * @return the user, or null if not found
     * @throws SQLException if a database error occurs
     */
    public User getUserByUsername(String username) throws SQLException {
        String sql = "SELECT id, username, password, email, is_admin FROM users WHERE username = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new User(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("email"),
                    rs.getInt("is_admin") == 1
                );
            } else {
                return null;
            }
        } finally {
            DatabaseUtil.closeQuietly(rs, stmt, conn);
        }
    }
    
    /**
     * Gets all users from the database.
     *
     * @return a list of all users
     * @throws SQLException if a database error occurs
     */
    public List<User> getAllUsers() throws SQLException {
        String sql = "SELECT id, username, password, email, is_admin FROM users";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            List<User> users = new ArrayList<>();
            
            while (rs.next()) {
                users.add(new User(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("email"),
                    rs.getInt("is_admin") == 1
                ));
            }
            
            return users;
        } finally {
            DatabaseUtil.closeQuietly(rs, stmt, conn);
        }
    }
    
    /**
     * Updates a user in the database.
     *
     * @param user the user to update
     * @return true if successful, false otherwise
     * @throws SQLException if a database error occurs
     */
    public boolean updateUser(User user) throws SQLException {
        String sql = "UPDATE users SET username = ?, email = ?, is_admin = ? WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setInt(3, user.isAdmin() ? 1 : 0);
            stmt.setInt(4, user.getId());
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } finally {
            DatabaseUtil.closeQuietly(stmt, conn);
        }
    }
    
    /**
     * Updates a user's password.
     *
     * @param userId the ID of the user
     * @param newPassword the new password (plain text, will be hashed)
     * @return true if successful, false otherwise
     * @throws SQLException if a database error occurs
     */
    public boolean updatePassword(int userId, String newPassword) throws SQLException {
        String sql = "UPDATE users SET password = ? WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            
            stmt.setString(1, PasswordUtil.hashPassword(newPassword));
            stmt.setInt(2, userId);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } finally {
            DatabaseUtil.closeQuietly(stmt, conn);
        }
    }
    
    /**
     * Deletes a user from the database.
     *
     * @param userId the ID of the user to delete
     * @return true if successful, false otherwise
     * @throws SQLException if a database error occurs
     */
    public boolean deleteUser(int userId) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } finally {
            DatabaseUtil.closeQuietly(stmt, conn);
        }
    }
    
    /**
     * Authenticates a user with the given username and password.
     *
     * @param username the username
     * @param password the plain text password
     * @return the authenticated user, or null if authentication fails
     * @throws SQLException if a database error occurs
     */
    public User authenticateUser(String username, String password) throws SQLException {
        User user = getUserByUsername(username);
        
        if (user != null && PasswordUtil.checkPassword(password, user.getPassword())) {
            return user;
        }
        
        return null;
    }
}