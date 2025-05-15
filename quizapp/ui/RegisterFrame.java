package com.quizapp.ui;

import com.quizapp.dao.UserDAO;
import com.quizapp.model.User;
import com.quizapp.util.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;

/**
 * Registration frame for new users.
 */
public class RegisterFrame extends JFrame {
    
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField emailField;
    private JButton registerButton;
    private JButton backButton;
    
    private UserDAO userDAO;
    private JFrame parentFrame;
    
    public RegisterFrame(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        userDAO = new UserDAO();
        
        // Set up the frame
        setTitle("Quiz Application - Register");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 400);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Create components
        initializeComponents();
        
        // Set up layout
        setupLayout();
        
        // Add action listeners
        addEventListeners();
    }
    
    private void initializeComponents() {
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        confirmPasswordField = new JPasswordField(20);
        emailField = new JTextField(20);
        registerButton = ThemeManager.createStyledButton("Register");
        backButton = ThemeManager.createStyledButton("Back to Login");
        
        // Set button colors
        backButton.setBackground(ThemeManager.SECONDARY_COLOR);
    }
    
    private void setupLayout() {
        // Create main panel with spacing
        JPanel mainPanel = ThemeManager.createStyledPanel();
        mainPanel.setLayout(new BorderLayout(ThemeManager.SPACING_MEDIUM, ThemeManager.SPACING_MEDIUM));
        
        // Create header
        JLabel titleLabel = ThemeManager.createTitleLabel("Create Account");
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(ThemeManager.BACKGROUND_COLOR);
        headerPanel.add(titleLabel);
        
        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(ThemeManager.BACKGROUND_COLOR);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(ThemeManager.SPACING_SMALL, ThemeManager.SPACING_SMALL, 
                               ThemeManager.SPACING_SMALL, ThemeManager.SPACING_SMALL);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Username field
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(ThemeManager.createStyledLabel("Username:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(usernameField, gbc);
        
        // Email field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(ThemeManager.createStyledLabel("Email:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(emailField, gbc);
        
        // Password field
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(ThemeManager.createStyledLabel("Password:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(passwordField, gbc);
        
        // Confirm password field
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(ThemeManager.createStyledLabel("Confirm Password:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(confirmPasswordField, gbc);
        
        // Buttons
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(ThemeManager.SPACING_MEDIUM, ThemeManager.SPACING_SMALL, 
                               ThemeManager.SPACING_SMALL, ThemeManager.SPACING_SMALL);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, ThemeManager.SPACING_MEDIUM, 0));
        buttonPanel.setBackground(ThemeManager.BACKGROUND_COLOR);
        buttonPanel.add(registerButton);
        buttonPanel.add(backButton);
        formPanel.add(buttonPanel, gbc);
        
        // Add components to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // Add main panel to frame
        setContentPane(mainPanel);
    }
    
    private void addEventListeners() {
        registerButton.addActionListener((ActionEvent e) -> {
            register();
        });
        
        backButton.addActionListener((ActionEvent e) -> {
            goBackToLogin();
        });
    }
    
    private void register() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        
        // Validate input
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill in all fields",
                    "Registration Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this,
                    "Passwords do not match",
                    "Registration Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!isValidEmail(email)) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid email address",
                    "Registration Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this,
                    "Password must be at least 6 characters long",
                    "Registration Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            // Check if username already exists
            if (userDAO.getUserByUsername(username) != null) {
                JOptionPane.showMessageDialog(this,
                        "Username already exists",
                        "Registration Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Create user
            User user = new User(username, password, email, false);
            user = userDAO.createUser(user);
            
            JOptionPane.showMessageDialog(this,
                    "Registration successful! You can now log in.",
                    "Registration Success",
                    JOptionPane.INFORMATION_MESSAGE);
            
            goBackToLogin();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Database error: " + ex.getMessage(),
                    "Registration Error",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private boolean isValidEmail(String email) {
        // Simple email validation
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
    
    private void goBackToLogin() {
        parentFrame.setVisible(true);
        dispose();
    }
}