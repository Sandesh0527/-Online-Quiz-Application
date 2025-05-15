package com.quizapp.ui.admin;

import com.quizapp.dao.QuizDAO;
import com.quizapp.model.Question;
import com.quizapp.model.Question.Option;
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
 * Dialog for creating and editing questions.
 */
public class QuestionEditorDialog extends JDialog {
    
    private Question question;
    private QuizDAO quizDAO;
    private boolean isNewQuestion;
    private boolean questionSaved;
    
    private JTextArea questionTextArea;
    private JSpinner pointsSpinner;
    private JTable optionsTable;
    private DefaultTableModel optionsTableModel;
    private JButton addOptionButton;
    private JButton editOptionButton;
    private JButton deleteOptionButton;
    private JButton saveButton;
    private JButton cancelButton;
    
    /**
     * Constructor for creating or editing a question.
     *
     * @param parent the parent dialog
     * @param question the question to edit, or a new question with quiz ID set
     * @param isNewQuestion true if creating a new question, false if editing
     */
    public QuestionEditorDialog(JDialog parent, Question question, boolean isNewQuestion) {
        super(parent, "Question Editor", true);
        this.question = question;
        this.quizDAO = new QuizDAO();
        this.isNewQuestion = isNewQuestion;
        this.questionSaved = false;
        
        // Set title based on mode
        setTitle(isNewQuestion ? "Add New Question" : "Edit Question");
        
        // Set up the dialog
        setSize(600, 500);
        setLocationRelativeTo(parent);
        setResizable(true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        // Initialize components
        initializeComponents();
        
        // Set up layout
        setupLayout();
        
        // Add action listeners
        addEventListeners();
        
        // Load question data
        loadQuestionData();
    }
    
    private void initializeComponents() {
        questionTextArea = new JTextArea(4, 30);
        questionTextArea.setLineWrap(true);
        questionTextArea.setWrapStyleWord(true);
        
        // Points spinner (1-10 points)
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(1, 1, 10, 1);
        pointsSpinner = new JSpinner(spinnerModel);
        
        // Options table
        String[] columnNames = {"ID", "Option Text", "Correct"};
        optionsTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 2) {
                    return Boolean.class; // For the correct column
                }
                return super.getColumnClass(columnIndex);
            }
        };
        optionsTable = new JTable(optionsTableModel);
        optionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        optionsTable.setRowHeight(ThemeManager.SPACING_LARGE);
        
        // Buttons
        addOptionButton = ThemeManager.createStyledButton("Add Option");
        editOptionButton = ThemeManager.createStyledButton("Edit Option");
        deleteOptionButton = ThemeManager.createStyledButton("Delete Option");
        saveButton = ThemeManager.createStyledButton("Save Question");
        cancelButton = ThemeManager.createStyledButton("Cancel");
        
        // Set button colors
        deleteOptionButton.setBackground(ThemeManager.ERROR_COLOR);
        cancelButton.setBackground(ThemeManager.SECONDARY_COLOR);
    }
    
    private void setupLayout() {
        JPanel mainPanel = new JPanel(new BorderLayout(ThemeManager.SPACING_MEDIUM, ThemeManager.SPACING_MEDIUM));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(
                ThemeManager.SPACING_MEDIUM, ThemeManager.SPACING_MEDIUM, 
                ThemeManager.SPACING_MEDIUM, ThemeManager.SPACING_MEDIUM));
        mainPanel.setBackground(ThemeManager.BACKGROUND_COLOR);
        
        // Question details panel
        JPanel questionDetailsPanel = new JPanel(new GridBagLayout());
        questionDetailsPanel.setBackground(ThemeManager.BACKGROUND_COLOR);
        questionDetailsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Question Details", 
                TitledBorder.LEFT, TitledBorder.TOP, ThemeManager.BODY_FONT));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(ThemeManager.SPACING_SMALL, ThemeManager.SPACING_SMALL, 
                               ThemeManager.SPACING_SMALL, ThemeManager.SPACING_SMALL);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Question text
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        questionDetailsPanel.add(ThemeManager.createStyledLabel("Question Text:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        JScrollPane questionScrollPane = new JScrollPane(questionTextArea);
        questionDetailsPanel.add(questionScrollPane, gbc);
        
        // Points
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        questionDetailsPanel.add(ThemeManager.createStyledLabel("Points:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        questionDetailsPanel.add(pointsSpinner, gbc);
        
        // Options panel
        JPanel optionsPanel = new JPanel(new BorderLayout(ThemeManager.SPACING_SMALL, ThemeManager.SPACING_SMALL));
        optionsPanel.setBackground(ThemeManager.BACKGROUND_COLOR);
        optionsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Options", 
                TitledBorder.LEFT, TitledBorder.TOP, ThemeManager.BODY_FONT));
        
        // Add table to scroll pane
        JScrollPane tableScrollPane = new JScrollPane(optionsTable);
        tableScrollPane.getViewport().setBackground(Color.WHITE);
        
        // Create option button panel
        JPanel optionButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, ThemeManager.SPACING_SMALL, 0));
        optionButtonPanel.setBackground(ThemeManager.BACKGROUND_COLOR);
        optionButtonPanel.add(addOptionButton);
        optionButtonPanel.add(editOptionButton);
        optionButtonPanel.add(deleteOptionButton);
        
        // Add components to options panel
        optionsPanel.add(tableScrollPane, BorderLayout.CENTER);
        optionsPanel.add(optionButtonPanel, BorderLayout.SOUTH);
        
        // Dialog button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, ThemeManager.SPACING_MEDIUM, 0));
        buttonPanel.setBackground(ThemeManager.BACKGROUND_COLOR);
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        
        // Add panels to main panel
        mainPanel.add(questionDetailsPanel, BorderLayout.NORTH);
        mainPanel.add(optionsPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Set content pane
        setContentPane(mainPanel);
    }
    
    private void addEventListeners() {
        addOptionButton.addActionListener((ActionEvent e) -> {
            addOption();
        });
        
        editOptionButton.addActionListener((ActionEvent e) -> {
            editOption();
        });
        
        deleteOptionButton.addActionListener((ActionEvent e) -> {
            deleteOption();
        });
        
        saveButton.addActionListener((ActionEvent e) -> {
            saveQuestion();
        });
        
        cancelButton.addActionListener((ActionEvent e) -> {
            dispose();
        });
    }
    
    private void loadQuestionData() {
        // Load question details
        questionTextArea.setText(question.getText());
        pointsSpinner.setValue(question.getPoints());
        
        // Load options
        updateOptionsTable();
    }
    
    private void updateOptionsTable() {
        // Clear existing data
        optionsTableModel.setRowCount(0);
        
        // Add options to table
        for (Option option : question.getOptions()) {
            optionsTableModel.addRow(new Object[]{
                option.getId(),
                option.getText(),
                option.isCorrect()
            });
        }
    }
    
    private void addOption() {
        OptionEditorDialog editorDialog = new OptionEditorDialog(this, null, true);
        editorDialog.setVisible(true);
        
        if (editorDialog.isOptionSaved()) {
            Option newOption = editorDialog.getOption();
            newOption.setQuestionId(question.getId());
            question.addOption(newOption);
            updateOptionsTable();
        }
    }
    
    private void editOption() {
        int selectedRow = optionsTable.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select an option to edit.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get the selected option
        Option selectedOption = null;
        int optionId = (int) optionsTable.getValueAt(selectedRow, 0);
        
        for (Option option : question.getOptions()) {
            if (option.getId() == optionId) {
                selectedOption = option;
                break;
            }
        }
        
        if (selectedOption != null) {
            OptionEditorDialog editorDialog = new OptionEditorDialog(this, selectedOption, false);
            editorDialog.setVisible(true);
            
            if (editorDialog.isOptionSaved()) {
                updateOptionsTable();
            }
        }
    }
    
    private void deleteOption() {
        int selectedRow = optionsTable.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select an option to delete.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get the selected option
        Option selectedOption = null;
        int optionId = (int) optionsTable.getValueAt(selectedRow, 0);
        
        for (int i = 0; i < question.getOptions().size(); i++) {
            if (question.getOptions().get(i).getId() == optionId) {
                selectedOption = question.getOptions().remove(i);
                break;
            }
        }
        
        if (selectedOption != null) {
            updateOptionsTable();
        }
    }
    
    private void saveQuestion() {
        // Validate input
        String questionText = questionTextArea.getText().trim();
        int points = (Integer) pointsSpinner.getValue();
        
        if (questionText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter question text.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (question.getOptions().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please add at least one option to the question.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        boolean hasCorrectOption = false;
        for (Option option : question.getOptions()) {
            if (option.isCorrect()) {
                hasCorrectOption = true;
                break;
            }
        }
        
        if (!hasCorrectOption) {
            JOptionPane.showMessageDialog(this,
                    "Please mark at least one option as correct.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Update question object
        question.setText(questionText);
        question.setPoints(points);
        
        try {
            if (isNewQuestion) {
                // Add new question to quiz
                question = quizDAO.addQuestionToQuiz(question.getQuizId(), question);
                questionSaved = true;
                
                JOptionPane.showMessageDialog(this,
                        "Question added successfully.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                // Update existing question
                boolean success = quizDAO.updateQuestion(question);
                
                if (success) {
                    questionSaved = true;
                    JOptionPane.showMessageDialog(this,
                            "Question updated successfully.",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Failed to update question.",
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
    
    /**
     * Checks if the question was successfully saved.
     *
     * @return true if the question was saved, false otherwise
     */
    public boolean isQuestionSaved() {
        return questionSaved;
    }
    
    /**
     * Gets the question object.
     *
     * @return the question
     */
    public Question getQuestion() {
        return question;
    }
}