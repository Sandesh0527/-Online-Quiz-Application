package com.quizapp.ui.user;

import com.quizapp.dao.UserDAO;
import com.quizapp.model.User;
import com.quizapp.util.PasswordUtil;
import com.quizapp.util.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;

/**
 * Panel for user profile management.
 */
public class UserProfilePanel extends JPanel {
    
    private User currentUser;
    private UserDAO userDAO;
    
    private JTextField usernameField;
    private JTextField emailField;
    private JPasswordField currentPasswordField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private JButton updateProfileButton;
    private JButton updatePasswordButton;
    
    public UserProfilePanel(User currentUser) {
        this.currentUser = currentUser;
        this.userDAO = new UserDAO();
        
        setLayout(new BorderLayout(ThemeManager.SPACING_MEDIUM, ThemeManager.SPACING_MEDIUM));
        setBackground(ThemeManager.BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(
                ThemeManager.SPACING_MEDIUM, ThemeManager.SPACING_MEDIUM, 
                ThemeManager.SPACING_MEDIUM, ThemeManager.SPACING_MEDIUM));
        
        // Initialize components
        initializeComponents();
        
        // Set up layout
        setupLayout();
        
        // Add action listeners
        addEventListeners();
    }
    
    private void initializeComponents() {
        // Profile fields
        usernameField = new JTextField(currentUser.getUsername(), 20);
        emailField = new JTextField(currentUser.getEmail(), 20);
        
        // Password fields
        currentPasswordField = new JPasswordField(20);
        newPasswordField = new JPasswordField(20);
        confirmPasswordField = new JPasswordField(20);
        
        // Buttons
        updateProfileButton = ThemeManager.createStyledButton("Update Profile");
        updatePasswordButton = ThemeManager.createStyledButton("Change Password");
    }
    
    private void setupLayout() {
        // Create title label
        JLabel titleLabel = ThemeManager.createTitleLabel("User Profile");
        
        // Create profile panel
        JPanel profilePanel = createProfilePanel();
        
        // Create password panel
        JPanel passwordPanel = createPasswordPanel();
        
        // Add panels to a tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(ThemeManager.BODY_FONT);
        tabbedPane.addTab("Profile", profilePanel);
        tabbedPane.addTab("Change Password", passwordPanel);
        
        // Add components to main panel
        add(titleLabel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(ThemeManager.BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(
                ThemeManager.SPACING_MEDIUM, ThemeManager.SPACING_MEDIUM, 
                ThemeManager.SPACING_MEDIUM, ThemeManager.SPACING_MEDIUM));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(ThemeManager.SPACING_SMALL, ThemeManager.SPACING_SMALL, 
                               ThemeManager.SPACING_SMALL, ThemeManager.SPACING_SMALL);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Username field
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(ThemeManager.createStyledLabel("Username:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(usernameField, gbc);
        
        // Email field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(ThemeManager.createStyledLabel("Email:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(emailField, gbc);
        
        // Account type
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(ThemeManager.createStyledLabel("Account Type:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel accountTypeLabel = ThemeManager.createStyledLabel(
                currentUser.isAdmin() ? "Administrator" : "Regular User");
        accountTypeLabel.setForeground(currentUser.isAdmin() ? ThemeManager.PRIMARY_COLOR : ThemeManager.TEXT_PRIMARY);
        panel.add(accountTypeLabel, gbc);
        
        // Update button
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(ThemeManager.SPACING_LARGE, ThemeManager.SPACING_SMALL, 
                               ThemeManager.SPACING_SMALL, ThemeManager.SPACING_SMALL);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(ThemeManager.BACKGROUND_COLOR);
        buttonPanel.add(updateProfileButton);
        panel.add(buttonPanel, gbc);
        
        return panel;
    }
    
    private JPanel createPasswordPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(ThemeManager.BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(
                ThemeManager.SPACING_MEDIUM, ThemeManager.SPACING_MEDIUM, 
                ThemeManager.SPACING_MEDIUM, ThemeManager.SPACING_MEDIUM));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(ThemeManager.SPACING_SMALL, ThemeManager.SPACING_SMALL, 
                               ThemeManager.SPACING_SMALL, ThemeManager.SPACING_SMALL);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Current password field
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(ThemeManager.createStyledLabel("Current Password:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(currentPasswordField, gbc);
        
        // New password field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(ThemeManager.createStyledLabel("New Password:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(newPasswordField, gbc);
        
        // Confirm password field
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(ThemeManager.createStyledLabel("Confirm New Password:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(confirmPasswordField, gbc);
        
        // Update button
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(ThemeManager.SPACING_LARGE, ThemeManager.SPACING_SMALL, 
                               ThemeManager.SPACING_SMALL, ThemeManager.SPACING_SMALL);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(ThemeManager.BACKGROUND_COLOR);
        buttonPanel.add(updatePasswordButton);
        panel.add(buttonPanel, gbc);
        
        return panel;
    }
    
    private void addEventListeners() {
        updateProfileButton.addActionListener((ActionEvent e) -> {
            updateProfile();
        });
        
        updatePasswordButton.addActionListener((ActionEvent e) -> {
            updatePassword();
        });
    }
    
    private void updateProfile() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        
        // Validate input
        if (username.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill in all fields",
                    "Update Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!isValidEmail(email)) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid email address",
                    "Update Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            // Check if username is changed and already exists
            if (!username.equals(currentUser.getUsername()) && userDAO.getUserByUsername(username) != null) {
                JOptionPane.showMessageDialog(this,
                        "Username already exists",
                        "Update Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Update user
            currentUser.setUsername(username);
            currentUser.setEmail(email);
            
            boolean success = userDAO.updateUser(currentUser);
            
            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Profile updated successfully",
                        "Update Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to update profile",
                        "Update Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Database error: " + ex.getMessage(),
                    "Update Error",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private void updatePassword() {
        String currentPassword = new String(currentPasswordField.getPassword());
        String newPassword = new String(newPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        
        // Validate input
        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill in all password fields",
                    "Update Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this,
                    "New passwords do not match",
                    "Update Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (newPassword.length() < 6) {
            JOptionPane.showMessageDialog(this,
                    "Password must be at least 6 characters long",
                    "Update Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            // Verify current password
            User user = userDAO.authenticateUser(currentUser.getUsername(), currentPassword);
            
            if (user == null) {
                JOptionPane.showMessageDialog(this,
                        "Current password is incorrect",
                        "Update Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Update password
            boolean success = userDAO.updatePassword(currentUser.getId(), newPassword);
            
            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Password updated successfully",
                        "Update Success",
                        JOptionPane.INFORMATION_MESSAGE);
                
                // Clear password fields
                currentPasswordField.setText("");
                newPasswordField.setText("");
                confirmPasswordField.setText("");
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to update password",
                        "Update Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Database error: " + ex.getMessage(),
                    "Update Error",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private boolean isValidEmail(String email) {
        // Simple email validation
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
}