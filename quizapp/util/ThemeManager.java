package com.quizapp.util;

import javax.swing.*;
import java.awt.*;

/**
 * Manages the application theme and UI constants.
 */
public class ThemeManager {
    // Color scheme
    public static final Color PRIMARY_COLOR = new Color(51, 102, 204);     // Blue
    public static final Color SECONDARY_COLOR = new Color(0, 153, 102);    // Green
    public static final Color ACCENT_COLOR = new Color(255, 153, 0);       // Orange
    public static final Color SUCCESS_COLOR = new Color(46, 204, 113);     // Green
    public static final Color WARNING_COLOR = new Color(241, 196, 15);     // Yellow
    public static final Color ERROR_COLOR = new Color(231, 76, 60);        // Red
    public static final Color BACKGROUND_COLOR = new Color(245, 245, 250); // Light gray
    public static final Color TEXT_PRIMARY = new Color(51, 51, 51);        // Dark gray
    public static final Color TEXT_SECONDARY = new Color(119, 119, 119);   // Medium gray
    
    // Font settings
    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font HEADING_FONT = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font BODY_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font SMALL_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    
    // Spacing (following 8px grid)
    public static final int SPACING_SMALL = 8;
    public static final int SPACING_MEDIUM = 16;
    public static final int SPACING_LARGE = 24;
    public static final int SPACING_XLARGE = 32;
    
    // Border radius
    public static final int BORDER_RADIUS = 8;
    
    /**
     * Sets the application look and feel.
     */
    public static void setLookAndFeel() {
        try {
            // Use system look and feel as a base
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Override some UI defaults
            UIManager.put("Panel.background", BACKGROUND_COLOR);
            UIManager.put("OptionPane.background", BACKGROUND_COLOR);
            UIManager.put("Button.background", PRIMARY_COLOR);
            UIManager.put("Button.foreground", Color.WHITE);
            UIManager.put("Button.font", BODY_FONT);
            UIManager.put("Label.font", BODY_FONT);
            UIManager.put("TextField.font", BODY_FONT);
            UIManager.put("TextArea.font", BODY_FONT);
            UIManager.put("ComboBox.font", BODY_FONT);
            UIManager.put("PasswordField.font", BODY_FONT);
            UIManager.put("CheckBox.font", BODY_FONT);
            UIManager.put("RadioButton.font", BODY_FONT);
            UIManager.put("TabbedPane.font", BODY_FONT);
            UIManager.put("Table.font", BODY_FONT);
            UIManager.put("TableHeader.font", BODY_FONT.deriveFont(Font.BOLD));
            
        } catch (Exception e) {
            System.err.println("Error setting look and feel: " + e.getMessage());
        }
    }
    
    /**
     * Creates a styled button with the application's theme.
     */
    public static JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(BODY_FONT);
        button.setBorder(BorderFactory.createEmptyBorder(SPACING_SMALL, SPACING_MEDIUM, SPACING_SMALL, SPACING_MEDIUM));
        return button;
    }
    
    /**
     * Creates a styled panel with the application's theme.
     */
    public static JPanel createStyledPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(SPACING_MEDIUM, SPACING_MEDIUM, SPACING_MEDIUM, SPACING_MEDIUM));
        return panel;
    }
    
    /**
     * Creates a styled label with the application's theme.
     */
    public static JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(BODY_FONT);
        label.setForeground(TEXT_PRIMARY);
        return label;
    }
    
    /**
     * Creates a styled title label with the application's theme.
     */
    public static JLabel createTitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(TITLE_FONT);
        label.setForeground(PRIMARY_COLOR);
        return label;
    }
}