package com.quizapp.ui;

import com.quizapp.model.User;
import com.quizapp.util.ThemeManager;
import com.quizapp.ui.admin.AdminPanel;
import com.quizapp.ui.quiz.QuizListPanel;
import com.quizapp.ui.user.UserProfilePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Main application frame after user login.
 */
public class MainFrame extends JFrame {
    
    private User currentUser;
    private JPanel contentPanel;
    private JButton quizListButton;
    private JButton myResultsButton;
    private JButton profileButton;
    private JButton adminButton;
    private JButton logoutButton;
    
    public MainFrame(User currentUser) {
        this.currentUser = currentUser;
        
        // Set up the frame
        setTitle("Quiz Application - Welcome " + currentUser.getUsername());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(800, 500));
        
        // Create components
        initializeComponents();
        
        // Set up layout
        setupLayout();
        
        // Add action listeners
        addEventListeners();
        
        // Show quiz list by default
        showQuizList();
    }
    
    private void initializeComponents() {
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(ThemeManager.BACKGROUND_COLOR);
        
        quizListButton = ThemeManager.createStyledButton("Available Quizzes");
        myResultsButton = ThemeManager.createStyledButton("My Results");
        profileButton = ThemeManager.createStyledButton("Profile");
        adminButton = ThemeManager.createStyledButton("Admin Panel");
        logoutButton = ThemeManager.createStyledButton("Logout");
        
        // Only show admin button for admin users
        adminButton.setVisible(currentUser.isAdmin());
        
        // Style logout button differently
        logoutButton.setBackground(ThemeManager.ERROR_COLOR);
    }
    
    private void setupLayout() {
        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Create sidebar panel
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(new Color(42, 58, 80)); // Dark blue background
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(
                ThemeManager.SPACING_LARGE, ThemeManager.SPACING_MEDIUM, 
                ThemeManager.SPACING_LARGE, ThemeManager.SPACING_MEDIUM));
        
        // Add app title to sidebar
        JLabel titleLabel = new JLabel("Quiz App");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(ThemeManager.TITLE_FONT);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, ThemeManager.SPACING_LARGE, 0));
        
        // Create a container for the title for proper alignment
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(new Color(42, 58, 80));
        titlePanel.add(titleLabel);
        titlePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, titlePanel.getPreferredSize().height));
        sidebarPanel.add(titlePanel);
        
        // Add sidebar buttons
        for (JButton button : new JButton[]{quizListButton, myResultsButton, profileButton, adminButton}) {
            styleNavigationButton(button);
            sidebarPanel.add(button);
            sidebarPanel.add(Box.createRigidArea(new Dimension(0, ThemeManager.SPACING_SMALL)));
        }
        
        // Add spacer to push logout button to bottom
        sidebarPanel.add(Box.createVerticalGlue());
        
        // Add logout button
        styleNavigationButton(logoutButton);
        sidebarPanel.add(logoutButton);
        
        // Add sidebar and content panel to main panel
        mainPanel.add(sidebarPanel, BorderLayout.WEST);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Set main panel as content pane
        setContentPane(mainPanel);
    }
    
    private void styleNavigationButton(JButton button) {
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(200, button.getPreferredSize().height));
        button.setFont(ThemeManager.BODY_FONT);
        button.setFocusPainted(false);
        button.setBackground(new Color(60, 80, 110)); // Slightly lighter blue
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(
                ThemeManager.SPACING_SMALL, ThemeManager.SPACING_MEDIUM, 
                ThemeManager.SPACING_SMALL, ThemeManager.SPACING_MEDIUM));
    }
    
    private void addEventListeners() {
        quizListButton.addActionListener((ActionEvent e) -> {
            showQuizList();
        });
        
        myResultsButton.addActionListener((ActionEvent e) -> {
            showMyResults();
        });
        
        profileButton.addActionListener((ActionEvent e) -> {
            showProfile();
        });
        
        adminButton.addActionListener((ActionEvent e) -> {
            showAdminPanel();
        });
        
        logoutButton.addActionListener((ActionEvent e) -> {
            logout();
        });
    }
    
    private void showQuizList() {
        setActiveButton(quizListButton);
        contentPanel.removeAll();
        QuizListPanel quizListPanel = new QuizListPanel(currentUser);
        contentPanel.add(quizListPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private void showMyResults() {
        setActiveButton(myResultsButton);
        contentPanel.removeAll();
        ResultsPanel resultsPanel = new ResultsPanel(currentUser);
        contentPanel.add(resultsPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private void showProfile() {
        setActiveButton(profileButton);
        contentPanel.removeAll();
        UserProfilePanel profilePanel = new UserProfilePanel(currentUser);
        contentPanel.add(profilePanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private void showAdminPanel() {
        if (!currentUser.isAdmin()) {
            JOptionPane.showMessageDialog(this,
                    "You do not have administrator privileges.",
                    "Access Denied",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        setActiveButton(adminButton);
        contentPanel.removeAll();
        AdminPanel adminPanel = new AdminPanel(currentUser);
        contentPanel.add(adminPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private void logout() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION);
        
        if (choice == JOptionPane.YES_OPTION) {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
            dispose();
        }
    }
    
    private void setActiveButton(JButton activeButton) {
        // Reset all buttons
        for (JButton button : new JButton[]{quizListButton, myResultsButton, profileButton, adminButton}) {
            if (button != activeButton) {
                button.setBackground(new Color(60, 80, 110)); // Default color
            }
        }
        
        // Set active button
        activeButton.setBackground(ThemeManager.PRIMARY_COLOR);
    }
}