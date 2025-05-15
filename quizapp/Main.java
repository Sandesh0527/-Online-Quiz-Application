package com.quizapp;

import com.quizapp.dao.DatabaseInitializer;
import com.quizapp.ui.LoginFrame;
import com.quizapp.util.ThemeManager;

import javax.swing.*;
import java.awt.*;

/**
 * Main class that serves as the entry point for the Online Quiz Application.
 */
public class Main {
    public static void main(String[] args) {
        try {
            // Initialize database
            DatabaseInitializer.initializeDatabase();
            
            // Set application look and feel
            ThemeManager.setLookAndFeel();
            
            // Start with the login screen
            SwingUtilities.invokeLater(() -> {
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
            });
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "An error occurred during application startup: " + e.getMessage(),
                    "Startup Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}