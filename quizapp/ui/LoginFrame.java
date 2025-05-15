package com.quizapp.ui;

import com.quizapp.dao.UserDAO;
import com.quizapp.model.User;
import com.quizapp.util.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;

/**
 * Login frame for user authentication.
 */
public class LoginFrame extends JFrame {
    
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    
    private UserDAO userDAO;
    
    public LoginFrame() {
        userDAO = new UserDAO();
        
        // Set up the frame
        setTitle("Quiz Application - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
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
        loginButton = ThemeManager.createStyledButton("Login");
        registerButton = ThemeManager.createStyledButton("Register");
        
        // Set button colors
        registerButton.setBackground(ThemeManager.SECONDARY_COLOR);
    }
    
    private void setupLayout() {
        // Create main panel with spacing
        JPanel mainPanel = ThemeManager.createStyledPanel();
        mainPanel.setLayout(new BorderLayout(ThemeManager.SPACING_MEDIUM, ThemeManager.SPACING_MEDIUM));
        
        // Create logo/header
        JLabel titleLabel = ThemeManager.createTitleLabel("Quiz Application");
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
        
        // Password field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(ThemeManager.createStyledLabel("Password:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(passwordField, gbc);
        
        // Login button
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(ThemeManager.SPACING_MEDIUM, ThemeManager.SPACING_SMALL, 
                               ThemeManager.SPACING_SMALL, ThemeManager.SPACING_SMALL);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, ThemeManager.SPACING_MEDIUM, 0));
        buttonPanel.setBackground(ThemeManager.BACKGROUND_COLOR);
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        formPanel.add(buttonPanel, gbc);
        
        // Add components to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // Add information about default admin account
        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(ThemeManager.BACKGROUND_COLOR);
        infoPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        JLabel infoLabel = new JLabel("Default admin: username 'admin', password 'admin123'");
        infoLabel.setFont(ThemeManager.SMALL_FONT);
        infoLabel.setForeground(ThemeManager.TEXT_SECONDARY);
        infoPanel.add(infoLabel);
        mainPanel.add(infoPanel, BorderLayout.SOUTH);
        
        // Add main panel to frame
        setContentPane(mainPanel);
    }
    
    private void addEventListeners() {
        loginButton.addActionListener((ActionEvent e) -> {
            login();
        });
        
        registerButton.addActionListener((ActionEvent e) -> {
            openRegisterFrame();
        });
        
        // Allow login on Enter key
        passwordField.addActionListener((ActionEvent e) -> {
            login();
        });
    }
    
    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter both username and password",
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            User user = userDAO.authenticateUser(username, password);
            
            if (user != null) {
                // Login successful
                openMainApplication(user);
            } else {
                // Login failed
                JOptionPane.showMessageDialog(this,
                        "Invalid username or password",
                        "Login Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Database error: " + ex.getMessage(),
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private void openRegisterFrame() {
        RegisterFrame registerFrame = new RegisterFrame(this);
        registerFrame.setVisible(true);
        setVisible(false);
    }
    
    private void openMainApplication(User user) {
        MainFrame mainFrame = new MainFrame(user);
        mainFrame.setVisible(true);
        dispose();
    }
}