package com.quizapp.ui;

import com.quizapp.dao.QuizResultDAO;
import com.quizapp.model.QuizResult;
import com.quizapp.model.User;
import com.quizapp.util.ThemeManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Panel to display a user's quiz results.
 */
public class ResultsPanel extends JPanel {
    
    private User currentUser;
    private QuizResultDAO quizResultDAO;
    private JTable resultsTable;
    private DefaultTableModel tableModel;
    private JLabel titleLabel;
    private JButton refreshButton;
    private JButton viewDetailsButton;
    
    public ResultsPanel(User currentUser) {
        this.currentUser = currentUser;
        this.quizResultDAO = new QuizResultDAO();
        
        setLayout(new BorderLayout(ThemeManager.SPACING_MEDIUM, ThemeManager.SPACING_MEDIUM));
        setBackground(ThemeManager.BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(
                ThemeManager.SPACING_MEDIUM, ThemeManager.SPACING_MEDIUM, 
                ThemeManager.SPACING_MEDIUM, ThemeManager.SPACING_MEDIUM));
        
        // Initialize components
        initializeComponents();
        
        // Set up layout
        setupLayout();
        
        // Load quiz results
        loadQuizResults();
    }
    
    private void initializeComponents() {
        titleLabel = ThemeManager.createTitleLabel("My Quiz Results");
        
        // Create table model with column names
        String[] columnNames = {"Quiz", "Score", "Percentage", "Completion Date", "Duration"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table cells non-editable
            }
        };
        
        // Create table with the model
        resultsTable = new JTable(tableModel);
        resultsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resultsTable.setRowHeight(ThemeManager.SPACING_LARGE);
        resultsTable.getTableHeader().setFont(ThemeManager.BODY_FONT.deriveFont(Font.BOLD));
        resultsTable.setFont(ThemeManager.BODY_FONT);
        
        // Create buttons
        refreshButton = ThemeManager.createStyledButton("Refresh");
        viewDetailsButton = ThemeManager.createStyledButton("View Details");
    }
    
    private void setupLayout() {
        // Create header panel with title and refresh button
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ThemeManager.BACKGROUND_COLOR);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(ThemeManager.BACKGROUND_COLOR);
        buttonPanel.add(refreshButton);
        buttonPanel.add(viewDetailsButton);
        headerPanel.add(buttonPanel, BorderLayout.EAST);
        
        // Create scroll pane for table
        JScrollPane scrollPane = new JScrollPane(resultsTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        // Add components to panel
        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        
        // Add action listeners
        refreshButton.addActionListener(e -> loadQuizResults());
        viewDetailsButton.addActionListener(e -> viewResultDetails());
    }
    
    private void loadQuizResults() {
        // Clear existing data
        tableModel.setRowCount(0);
        
        try {
            // Get quiz results for the current user
            List<QuizResult> results = quizResultDAO.getQuizResultsByUser(currentUser.getId());
            
            // Add results to table
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            
            for (QuizResult result : results) {
                String quizTitle = result.getQuizTitle();
                String score = result.getScore() + " / " + result.getMaxScore();
                String percentage = String.format("%.1f%%", result.getPercentageScore());
                String completionDate = result.getCompletedAt().format(dateFormatter);
                
                // Format duration
                long durationSeconds = result.getDurationInSeconds();
                String duration = formatDuration(durationSeconds);
                
                // Add row to table
                tableModel.addRow(new Object[]{quizTitle, score, percentage, completionDate, duration});
            }
            
            // If no results, show message
            if (results.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "You haven't taken any quizzes yet.",
                        "No Results",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading quiz results: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private String formatDuration(long seconds) {
        long minutes = seconds / 60;
        long remainingSeconds = seconds % 60;
        
        if (minutes > 0) {
            return String.format("%d min %d sec", minutes, remainingSeconds);
        } else {
            return String.format("%d seconds", seconds);
        }
    }
    
    private void viewResultDetails() {
        int selectedRow = resultsTable.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a result to view details.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // In a real implementation, you would get the result ID and show detailed view
        JOptionPane.showMessageDialog(this,
                "Detailed result view would be displayed here.",
                "Result Details",
                JOptionPane.INFORMATION_MESSAGE);
    }
}