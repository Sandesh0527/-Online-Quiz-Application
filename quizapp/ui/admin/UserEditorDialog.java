package com.quizapp.ui.admin;

import com.quizapp.dao.UserDAO;
import com.quizapp.model.User;
import com.quizapp.util.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;

/**
 * Dialog for editing user information.
 */
public class UserEditorDialog extends JDialog {
    
    private User user;
    private UserDAO userDAO;
    
    private JTextField usernameField;
    private JTextField emailField;
    private JCheckBox adminCheckBox;
    private JButton saveButton;
    private JButton cancelButton;
    
    /**
     * Constructor for the user editor dialog.
     *
     * @param parent the parent frame
     * @param user the user to edit
     */
    public UserEditorDialog(JFrame parent, User user) {
        super(parent, "Edit User", true);
        this.user = user;
        this.userDAO = new UserDAO();
        
        // Set up the dialog
        setTitle("Edit User: " + user.getUsername());
        setSize(400, 250);
        setLocationRelativeTo(parent);
        setResizable(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        // Initialize components
        initializeComponents();
        
        // Set up layout
        setupLayout();
        
        // Add action listeners
        addEventListeners();
        
        // Load user data
        loadUserData();
    }
    
    private void initializeComponents() {
        usernameField = new JTextField(20);
        emailField = new JTextField(20);
        adminCheckBox = new JCheckBox("Administrator privileges");
        adminCheckBox.setFont(ThemeManager.BODY_FONT);
        adminCheckBox.setForeground(ThemeManager.TEXT_PRIMARY);
        
        saveButton = ThemeManager.createStyledButton("Save");
        cancelButton = ThemeManager.createStyledButton("Cancel");
        
        // Set button colors
        cancelButton.setBackground(ThemeManager.SECONDARY_COLOR);
    }
    
    private void setupLayout() {
        JPanel mainPanel = new JPanel(new BorderLayout(ThemeManager.SPACING_MEDIUM, ThemeManager.SPACING_MEDIUM));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(
                ThemeManager.SPACING_MEDIUM, ThemeManager.SPACING_MEDIUM, 
                ThemeManager.SPACING_MEDIUM, ThemeManager.SPACING_MEDIUM));
        mainPanel.setBackground(ThemeManager.BACKGROUND_COLOR);
        
        // User details panel
        JPanel userDetailsPanel = new JPanel(new GridBagLayout());
        userDetailsPanel.setBackground(ThemeManager.BACKGROUND_COLOR);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(ThemeManager.SPACING_SMALL, ThemeManager.SPACING_SMALL, 
                               ThemeManager.SPACING_SMALL, ThemeManager.SPACING_SMALL);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Username field
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        userDetailsPanel.add(ThemeManager.createStyledLabel("Username:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        userDetailsPanel.add(usernameField, gbc);
        
        // Email field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        userDetailsPanel.add(ThemeManager.createStyledLabel("Email:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        userDetailsPanel.add(emailField, gbc);
        
        // Admin checkbox
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        userDetailsPanel.add(adminCheckBox, gbc);
        
        // Reset password button
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton resetPasswordButton = ThemeManager.createStyledButton("Reset Password");
        resetPasswordButton.setBackground(ThemeManager.WARNING_COLOR);
        resetPasswordButton.addActionListener((ActionEvent e) -> {
            resetPassword();
        });
        userDetailsPanel.add(resetPasswordButton, gbc);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, ThemeManager.SPACING_MEDIUM, 0));
        buttonPanel.setBackground(ThemeManager.BACKGROUND_COLOR);
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        
        // Add panels to main panel
        mainPanel.add(userDetailsPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Set content pane
        setContentPane(mainPanel);
    }
    
    private void addEventListeners() {
        saveButton.addActionListener((ActionEvent e) -> {
            saveUser();
        });
        
        cancelButton.addActionListener((ActionEvent e) -> {
            dispose();
        });
    }
    
    private void loadUserData() {
        usernameField.setText(user.getUsername());
        emailField.setText(user.getEmail());
        adminCheckBox.setSelected(user.isAdmin());
    }
    
    private void saveUser() {
        // Validate input
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        boolean isAdmin = adminCheckBox.isSelected();
        
        if (username.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill in all fields",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!isValidEmail(email)) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid email address",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            // Check if username is changed and already exists
            if (!username.equals(user.getUsername()) && userDAO.getUserByUsername(username) != null) {
                JOptionPane.showMessageDialog(this,
                        "Username already exists",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Update user
            user.setUsername(username);
            user.setEmail(email);
            user.setAdmin(isAdmin);
            
            boolean success = userDAO.updateUser(user);
            
            if (success) {
                JOptionPane.showMessageDialog(this,
                        "User updated successfully",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to update user",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Database error: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private void resetPassword() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to reset the password for user '" + user.getUsername() + "'?",
                "Confirm Reset",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        
        if (choice == JOptionPane.YES_OPTION) {
            String newPassword = "password123"; // Default password
            
            try {
                boolean success = userDAO.updatePassword(user.getId(), newPassword);
                
                if (success) {
                    JOptionPane.showMessageDialog(this,
                            "Password has been reset to: " + newPassword + "\n" +
                            "Please inform the user to change it immediately after login.",
                            "Password Reset",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Failed to reset password",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                        "Database error: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
    
    private boolean isValidEmail(String email) {
        // Simple email validation
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
}