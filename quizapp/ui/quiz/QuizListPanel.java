package com.quizapp.ui.quiz;

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
 * Panel to display available quizzes.
 */
public class QuizListPanel extends JPanel {
    
    private User currentUser;
    private QuizDAO quizDAO;
    private JTable quizTable;
    private DefaultTableModel tableModel;
    private JButton takeQuizButton;
    private JButton refreshButton;
    private JTextField searchField;
    
    public QuizListPanel(User currentUser) {
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
        // Create title label
        JLabel titleLabel = ThemeManager.createTitleLabel("Available Quizzes");
        
        // Create search field
        searchField = new JTextField(20);
        searchField.setFont(ThemeManager.BODY_FONT);
        
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
        takeQuizButton = ThemeManager.createStyledButton("Take Quiz");
        refreshButton = ThemeManager.createStyledButton("Refresh");
    }
    
    private void setupLayout() {
        // Create search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(ThemeManager.BACKGROUND_COLOR);
        searchPanel.add(ThemeManager.createStyledLabel("Search:"));
        searchPanel.add(searchField);
        JButton searchButton = ThemeManager.createStyledButton("Search");
        searchButton.addActionListener((ActionEvent e) -> {
            searchQuizzes();
        });
        searchPanel.add(searchButton);
        
        // Create header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ThemeManager.BACKGROUND_COLOR);
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(ThemeManager.BACKGROUND_COLOR);
        titlePanel.add(ThemeManager.createTitleLabel("Available Quizzes"));
        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(searchPanel, BorderLayout.EAST);
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(ThemeManager.BACKGROUND_COLOR);
        buttonPanel.add(refreshButton);
        buttonPanel.add(takeQuizButton);
        
        // Create scroll pane for table
        JScrollPane scrollPane = new JScrollPane(quizTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        // Add components to main panel
        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void addEventListeners() {
        takeQuizButton.addActionListener((ActionEvent e) -> {
            takeQuiz();
        });
        
        refreshButton.addActionListener((ActionEvent e) -> {
            loadQuizzes();
        });
        
        // Double-click on row to take quiz
        quizTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    takeQuiz();
                }
            }
        });
        
        // Press Enter in search field to search
        searchField.addActionListener((ActionEvent e) -> {
            searchQuizzes();
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
            
            // Reset search field
            searchField.setText("");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading quizzes: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void searchQuizzes() {
        String searchTerm = searchField.getText().trim().toLowerCase();
        
        if (searchTerm.isEmpty()) {
            loadQuizzes();
            return;
        }
        
        // Clear existing data
        tableModel.setRowCount(0);
        
        try {
            // Get all quizzes
            List<Quiz> quizzes = quizDAO.getAllQuizzes();
            
            // Filter and add quizzes to table
            for (Quiz quiz : quizzes) {
                if (quiz.getTitle().toLowerCase().contains(searchTerm) || 
                    quiz.getDescription().toLowerCase().contains(searchTerm) ||
                    quiz.getCreatorName().toLowerCase().contains(searchTerm)) {
                    
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
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error searching quizzes: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void takeQuiz() {
        int selectedRow = quizTable.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a quiz to take.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int quizId = (int) quizTable.getValueAt(selectedRow, 0);
        
        try {
            Quiz quiz = quizDAO.getQuizById(quizId);
            
            if (quiz != null) {
                if (quiz.getQuestions().isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "This quiz has no questions yet.",
                            "Empty Quiz",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // Open quiz session in a new window
                JFrame mainFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
                QuizSessionFrame sessionFrame = new QuizSessionFrame(mainFrame, currentUser, quiz);
                sessionFrame.setVisible(true);
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
}