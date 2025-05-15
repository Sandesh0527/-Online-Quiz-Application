package com.quizapp.ui.admin;

import com.quizapp.dao.QuizDAO;
import com.quizapp.model.Quiz;
import com.quizapp.model.User;
import com.quizapp.util.ThemeManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.List;

/**
 * Panel for quiz management (creating, editing, and deleting quizzes).
 */
public class QuizManagementPanel extends JPanel {
    
    private User currentUser;
    private QuizDAO quizDAO;
    private JTable quizTable;
    private DefaultTableModel tableModel;
    private JButton createQuizButton;
    private JButton editQuizButton;
    private JButton deleteQuizButton;
    private JButton refreshButton;
    
    public QuizManagementPanel(User currentUser) {
        this.currentUser = currentUser;
        this.quizDAO = new QuizDAO();
        
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
        
        // Load quizzes
        loadQuizzes();
    }
    
    private void initializeComponents() {
        // Create table model with column names
        String[] columnNames = {"ID", "Title", "Description", "Creator", "Time Limit"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table cells non-editable
            }
        };
        
        // Create table with the model
        quizTable = new JTable(tableModel);
        quizTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        quizTable.setRowHeight(ThemeManager.SPACING_LARGE);
        quizTable.getTableHeader().setFont(ThemeManager.BODY_FONT.deriveFont(Font.BOLD));
        quizTable.setFont(ThemeManager.BODY_FONT);
        
        // Create buttons
        createQuizButton = ThemeManager.createStyledButton("Create Quiz");
        editQuizButton = ThemeManager.createStyledButton("Edit Quiz");
        deleteQuizButton = ThemeManager.createStyledButton("Delete Quiz");
        refreshButton = ThemeManager.createStyledButton("Refresh");
        
        // Set button colors
        deleteQuizButton.setBackground(ThemeManager.ERROR_COLOR);
    }
    
    private void setupLayout() {
        // Create title label
        JLabel titleLabel = ThemeManager.createStyledLabel("Quiz Management");
        titleLabel.setFont(ThemeManager.HEADING_FONT);
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, ThemeManager.SPACING_SMALL, 0));
        buttonPanel.setBackground(ThemeManager.BACKGROUND_COLOR);
        buttonPanel.add(createQuizButton);
        buttonPanel.add(editQuizButton);
        buttonPanel.add(deleteQuizButton);
        buttonPanel.add(Box.createHorizontalStrut(ThemeManager.SPACING_LARGE));
        buttonPanel.add(refreshButton);
        
        // Create header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ThemeManager.BACKGROUND_COLOR);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);
        
        // Create scroll pane for table
        JScrollPane scrollPane = new JScrollPane(quizTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        // Add components to panel
        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private void addEventListeners() {
        createQuizButton.addActionListener((ActionEvent e) -> {
            createQuiz();
        });
        
        editQuizButton.addActionListener((ActionEvent e) -> {
            editQuiz();
        });
        
        deleteQuizButton.addActionListener((ActionEvent e) -> {
            deleteQuiz();
        });
        
        refreshButton.addActionListener((ActionEvent e) -> {
            loadQuizzes();
        });
    }
    
    private void loadQuizzes() {
        // Clear existing data
        tableModel.setRowCount(0);
        
        try {
            // Get all quizzes
            List<Quiz> quizzes = quizDAO.getAllQuizzes();
            
            // Add quizzes to table
            for (Quiz quiz : quizzes) {
                String timeLimit = quiz.getTimeLimit() > 0 ? 
                        quiz.getTimeLimit() + " min" : "No limit";
                
                tableModel.addRow(new Object[]{
                    quiz.getId(),
                    quiz.getTitle(),
                    quiz.getDescription(),
                    quiz.getCreatorName(),
                    timeLimit
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading quizzes: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void createQuiz() {
        QuizEditorDialog editorDialog = new QuizEditorDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this), currentUser, null);
        editorDialog.setVisible(true);
        
        // Refresh table after dialog is closed
        loadQuizzes();
    }
    
    private void editQuiz() {
        int selectedRow = quizTable.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a quiz to edit.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int quizId = (int) quizTable.getValueAt(selectedRow, 0);
        
        try {
            Quiz quiz = quizDAO.getQuizById(quizId);
            
            if (quiz != null) {
                QuizEditorDialog editorDialog = new QuizEditorDialog(
                        (JFrame) SwingUtilities.getWindowAncestor(this), currentUser, quiz);
                editorDialog.setVisible(true);
                
                // Refresh table after dialog is closed
                loadQuizzes();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Quiz not found.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading quiz: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void deleteQuiz() {
        int selectedRow = quizTable.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a quiz to delete.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int quizId = (int) quizTable.getValueAt(selectedRow, 0);
        String quizTitle = (String) quizTable.getValueAt(selectedRow, 1);
        
        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete the quiz '" + quizTitle + "'? This cannot be undone.",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        
        if (choice == JOptionPane.YES_OPTION) {
            try {
                boolean success = quizDAO.deleteQuiz(quizId);
                
                if (success) {
                    JOptionPane.showMessageDialog(this,
                            "Quiz deleted successfully.",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    
                    // Refresh table
                    loadQuizzes();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Failed to delete quiz.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                        "Error deleting quiz: " + e.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
}