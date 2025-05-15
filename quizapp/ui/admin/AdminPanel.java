package com.quizapp.ui.admin;

import com.quizapp.dao.QuizDAO;
import com.quizapp.dao.UserDAO;
import com.quizapp.model.Quiz;
import com.quizapp.model.User;
import com.quizapp.util.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Panel for administrative functions.
 */
public class AdminPanel extends JPanel {
    
    private User currentUser;
    private QuizDAO quizDAO;
    private UserDAO userDAO;
    private JTabbedPane tabbedPane;
    
    public AdminPanel(User currentUser) {
        this.currentUser = currentUser;
        this.quizDAO = new QuizDAO();
        this.userDAO = new UserDAO();
        
        // Verify admin status
        if (!currentUser.isAdmin()) {
            throw new SecurityException("Only administrators can access this panel.");
        }
        
        setLayout(new BorderLayout(ThemeManager.SPACING_MEDIUM, ThemeManager.SPACING_MEDIUM));
        setBackground(ThemeManager.BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(
                ThemeManager.SPACING_MEDIUM, ThemeManager.SPACING_MEDIUM, 
                ThemeManager.SPACING_MEDIUM, ThemeManager.SPACING_MEDIUM));
        
        // Create components
        initializeComponents();
        
        // Set up layout
        setupLayout();
    }
    
    private void initializeComponents() {
        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(ThemeManager.BODY_FONT);
        
        // Create tabs
        JPanel quizManagementPanel = new QuizManagementPanel(currentUser);
        JPanel userManagementPanel = new UserManagementPanel(currentUser);
        
        // Add tabs
        tabbedPane.addTab("Quiz Management", quizManagementPanel);
        tabbedPane.addTab("User Management", userManagementPanel);
    }
    
    private void setupLayout() {
        // Create title label
        JLabel titleLabel = ThemeManager.createTitleLabel("Administration");
        
        // Add components to panel
        add(titleLabel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }
}