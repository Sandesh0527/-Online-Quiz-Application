package com.quizapp.ui.admin;

import com.quizapp.dao.QuizDAO;
import com.quizapp.model.Quiz;
import com.quizapp.model.Question;
import com.quizapp.model.Question.Option;
import com.quizapp.model.User;
import com.quizapp.util.ThemeManager;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Dialog for creating and editing quizzes.
 */
public class QuizEditorDialog extends JDialog {
    
    private User currentUser;
    private Quiz quiz;
    private QuizDAO quizDAO;
    private boolean isNewQuiz;
    
    private JTextField titleField;
    private JTextArea descriptionArea;
    private JSpinner timeLimitSpinner;
    private JTable questionsTable;
    private DefaultTableModel questionsTableModel;
    private JButton addQuestionButton;
    private JButton editQuestionButton;
    private JButton deleteQuestionButton;
    private JButton saveQuizButton;
    private JButton cancelButton;
    
    /**
     * Constructor for creating or editing a quiz.
     *
     * @param parent the parent frame
     * @param currentUser the current user
     * @param quiz the quiz to edit, or null for a new quiz
     */
    public QuizEditorDialog(JFrame parent, User currentUser, Quiz quiz) {
        super(parent, "Quiz Editor", true);
        this.currentUser = currentUser;
        this.quizDAO = new QuizDAO();
        
        // Determine if we're creating a new quiz or editing an existing one
        if (quiz == null) {
            this.quiz = new Quiz("", "", currentUser.getId(), 0);
            this.isNewQuiz = true;
            setTitle("Create New Quiz");
        } else {
            this.quiz = quiz;
            this.isNewQuiz = false;
            setTitle("Edit Quiz: " + quiz.getTitle());
        }
        
        // Set up the dialog
        setSize(700, 500);
        setLocationRelativeTo(parent);
        setResizable(true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        // Initialize components
        initializeComponents();
        
        // Set up layout
        setupLayout();
        
        // Add action listeners
        addEventListeners();
        
        // Load quiz data
        loadQuizData();
    }
    
    private void initializeComponents() {
        titleField = new JTextField(30);
        descriptionArea = new JTextArea(3, 30);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        
        // Time limit spinner (0-120 minutes, 0 means no limit)
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(0, 0, 120, 1);
        timeLimitSpinner = new JSpinner(spinnerModel);
        
        // Questions table
        String[] columnNames = {"ID", "Question", "Options", "Points"};
        questionsTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        questionsTable = new JTable(questionsTableModel);
        questionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        questionsTable.setRowHeight(ThemeManager.SPACING_LARGE);
        
        // Buttons
        addQuestionButton = ThemeManager.createStyledButton("Add Question");
        editQuestionButton = ThemeManager.createStyledButton("Edit Question");
        deleteQuestionButton = ThemeManager.createStyledButton("Delete Question");
        saveQuizButton = ThemeManager.createStyledButton("Save Quiz");
        cancelButton = ThemeManager.createStyledButton("Cancel");
        
        // Set button colors
        deleteQuestionButton.setBackground(ThemeManager.ERROR_COLOR);
        cancelButton.setBackground(ThemeManager.SECONDARY_COLOR);
    }
    
    private void setupLayout() {
        JPanel mainPanel = new JPanel(new BorderLayout(ThemeManager.SPACING_MEDIUM, ThemeManager.SPACING_MEDIUM));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(
                ThemeManager.SPACING_MEDIUM, ThemeManager.SPACING_MEDIUM, 
                ThemeManager.SPACING_MEDIUM, ThemeManager.SPACING_MEDIUM));
        mainPanel.setBackground(ThemeManager.BACKGROUND_COLOR);
        
        // Quiz details panel
        JPanel quizDetailsPanel = new JPanel(new GridBagLayout());
        quizDetailsPanel.setBackground(ThemeManager.BACKGROUND_COLOR);
        quizDetailsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Quiz Details", 
                TitledBorder.LEFT, TitledBorder.TOP, ThemeManager.BODY_FONT));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(ThemeManager.SPACING_SMALL, ThemeManager.SPACING_SMALL, 
                               ThemeManager.SPACING_SMALL, ThemeManager.SPACING_SMALL);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Title field
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        quizDetailsPanel.add(ThemeManager.createStyledLabel("Title:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        quizDetailsPanel.add(titleField, gbc);
        
        // Description field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        quizDetailsPanel.add(ThemeManager.createStyledLabel("Description:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        JScrollPane descriptionScrollPane = new JScrollPane(descriptionArea);
        quizDetailsPanel.add(descriptionScrollPane, gbc);
        
        // Time limit field
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        quizDetailsPanel.add(ThemeManager.createStyledLabel("Time Limit (min):"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        quizDetailsPanel.add(timeLimitSpinner, gbc);
        JLabel timeLimitNoteLabel = new JLabel("(0 = no limit)");
        timeLimitNoteLabel.setFont(ThemeManager.SMALL_FONT);
        timeLimitNoteLabel.setForeground(ThemeManager.TEXT_SECONDARY);
        quizDetailsPanel.add(timeLimitNoteLabel, gbc);
        
        // Questions panel
        JPanel questionsPanel = new JPanel(new BorderLayout(ThemeManager.SPACING_SMALL, ThemeManager.SPACING_SMALL));
        questionsPanel.setBackground(ThemeManager.BACKGROUND_COLOR);
        questionsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Questions", 
                TitledBorder.LEFT, TitledBorder.TOP, ThemeManager.BODY_FONT));
        
        // Add table to scroll pane
        JScrollPane tableScrollPane = new JScrollPane(questionsTable);
        tableScrollPane.getViewport().setBackground(Color.WHITE);
        
        // Create question button panel
        JPanel questionButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, ThemeManager.SPACING_SMALL, 0));
        questionButtonPanel.setBackground(ThemeManager.BACKGROUND_COLOR);
        questionButtonPanel.add(addQuestionButton);
        questionButtonPanel.add(editQuestionButton);
        questionButtonPanel.add(deleteQuestionButton);
        
        // Add components to questions panel
        questionsPanel.add(tableScrollPane, BorderLayout.CENTER);
        questionsPanel.add(questionButtonPanel, BorderLayout.SOUTH);
        
        // Dialog button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, ThemeManager.SPACING_MEDIUM, 0));
        buttonPanel.setBackground(ThemeManager.BACKGROUND_COLOR);
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveQuizButton);
        
        // Add panels to main panel
        mainPanel.add(quizDetailsPanel, BorderLayout.NORTH);
        mainPanel.add(questionsPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Set content pane
        setContentPane(mainPanel);
    }
    
    private void addEventListeners() {
        addQuestionButton.addActionListener((ActionEvent e) -> {
            addQuestion();
        });
        
        editQuestionButton.addActionListener((ActionEvent e) -> {
            editQuestion();
        });
        
        deleteQuestionButton.addActionListener((ActionEvent e) -> {
            deleteQuestion();
        });
        
        saveQuizButton.addActionListener((ActionEvent e) -> {
            saveQuiz();
        });
        
        cancelButton.addActionListener((ActionEvent e) -> {
            dispose();
        });
    }
    
    private void loadQuizData() {
        // Load quiz details
        titleField.setText(quiz.getTitle());
        descriptionArea.setText(quiz.getDescription());
        timeLimitSpinner.setValue(quiz.getTimeLimit());
        
        // Load questions
        updateQuestionsTable();
    }
    
    private void updateQuestionsTable() {
        // Clear existing data
        questionsTableModel.setRowCount(0);
        
        // Add questions to table
        for (Question question : quiz.getQuestions()) {
            int optionCount = question.getOptions().size();
            int correctCount = question.getCorrectOptions().size();
            
            String optionsInfo = optionCount + " options, " + correctCount + " correct";
            
            questionsTableModel.addRow(new Object[]{
                question.getId(),
                question.getText(),
                optionsInfo,
                question.getPoints()
            });
        }
    }
    
    private void addQuestion() {
        if (isNewQuiz) {
            JOptionPane.showMessageDialog(this,
                    "Please save the quiz first before adding questions.",
                    "Save Quiz",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        Question newQuestion = new Question(quiz.getId(), "", 1);
        QuestionEditorDialog editorDialog = new QuestionEditorDialog(this, newQuestion, true);
        editorDialog.setVisible(true);
        
        if (editorDialog.isQuestionSaved()) {
            Question savedQuestion = editorDialog.getQuestion();
            quiz.addQuestion(savedQuestion);
            updateQuestionsTable();
        }
    }
    
    private void editQuestion() {
        int selectedRow = questionsTable.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a question to edit.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get the selected question
        Question selectedQuestion = null;
        int questionId = (int) questionsTable.getValueAt(selectedRow, 0);
        
        for (Question question : quiz.getQuestions()) {
            if (question.getId() == questionId) {
                selectedQuestion = question;
                break;
            }
        }
        
        if (selectedQuestion != null) {
            QuestionEditorDialog editorDialog = new QuestionEditorDialog(this, selectedQuestion, false);
            editorDialog.setVisible(true);
            
            if (editorDialog.isQuestionSaved()) {
                updateQuestionsTable();
            }
        }
    }
    
    private void deleteQuestion() {
        int selectedRow = questionsTable.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a question to delete.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int questionId = (int) questionsTable.getValueAt(selectedRow, 0);
        
        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this question?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        
        if (choice == JOptionPane.YES_OPTION) {
            try {
                boolean success = quizDAO.deleteQuestion(questionId);
                
                if (success) {
                    // Remove question from list
                    for (int i = 0; i < quiz.getQuestions().size(); i++) {
                        if (quiz.getQuestions().get(i).getId() == questionId) {
                            quiz.getQuestions().remove(i);
                            break;
                        }
                    }
                    
                    updateQuestionsTable();
                    
                    JOptionPane.showMessageDialog(this,
                            "Question deleted successfully.",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Failed to delete question.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                        "Error deleting question: " + e.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
    
    private void saveQuiz() {
        // Validate input
        String title = titleField.getText().trim();
        String description = descriptionArea.getText().trim();
        int timeLimit = (Integer) timeLimitSpinner.getValue();
        
        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a title for the quiz.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Update quiz object
        quiz.setTitle(title);
        quiz.setDescription(description);
        quiz.setTimeLimit(timeLimit);
        
        try {
            if (isNewQuiz) {
                // Create new quiz
                quiz = quizDAO.createQuiz(quiz);
                isNewQuiz = false;
                setTitle("Edit Quiz: " + quiz.getTitle());
                
                JOptionPane.showMessageDialog(this,
                        "Quiz created successfully. You can now add questions.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Update existing quiz
                boolean success = quizDAO.updateQuiz(quiz);
                
                if (success) {
                    JOptionPane.showMessageDialog(this,
                            "Quiz updated successfully.",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Failed to update quiz.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Database error: " + e.getMessage(),
                    "Save Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}