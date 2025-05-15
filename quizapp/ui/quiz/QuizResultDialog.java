package com.quizapp.ui.quiz;

import com.quizapp.model.QuizResult;
import com.quizapp.model.QuizResult.QuestionResult;
import com.quizapp.util.ThemeManager;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Dialog to display quiz results.
 */
public class QuizResultDialog extends JDialog {
    
    private QuizResult result;
    
    private JLabel scoreLabel;
    private JLabel percentageLabel;
    private JLabel timeLabel;
    private JTable questionsTable;
    private DefaultTableModel tableModel;
    private JButton closeButton;
    
    /**
     * Constructor for the quiz result dialog.
     *
     * @param parent the parent frame
     * @param result the quiz result to display
     */
    public QuizResultDialog(JFrame parent, QuizResult result) {
        super(parent, "Quiz Results", true);
        this.result = result;
        
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
        
        // Load result data
        loadResultData();
    }
    
    private void initializeComponents() {
        // Create score labels
        scoreLabel = new JLabel("");
        scoreLabel.setFont(ThemeManager.TITLE_FONT);
        scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        percentageLabel = new JLabel("");
        percentageLabel.setFont(ThemeManager.HEADING_FONT);
        percentageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        timeLabel = new JLabel("");
        timeLabel.setFont(ThemeManager.BODY_FONT);
        timeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Create table for questions and answers
        String[] columnNames = {"Question", "Your Answer", "Correct", "Points"};
        tableModel = new DefaultTableModel(columnNames, 0) {
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
        
        questionsTable = new JTable(tableModel);
        questionsTable.setRowHeight(ThemeManager.SPACING_LARGE);
        questionsTable.getTableHeader().setFont(ThemeManager.BODY_FONT.deriveFont(Font.BOLD));
        questionsTable.setFont(ThemeManager.BODY_FONT);
        
        // Custom renderer to color rows based on correctness
        questionsTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                         boolean isSelected, boolean hasFocus,
                                                         int row, int column) {
                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                
                // Get the correct status from the "Correct" column (index 2)
                Boolean isCorrect = (Boolean) table.getValueAt(row, 2);
                
                if (!isSelected) {
                    // Set background color based on correctness
                    if (isCorrect) {
                        c.setBackground(new Color(240, 255, 240)); // Light green
                    } else {
                        c.setBackground(new Color(255, 240, 240)); // Light red
                    }
                }
                
                return c;
            }
        });
        
        // Create close button
        closeButton = ThemeManager.createStyledButton("Close");
    }
    
    private void setupLayout() {
        JPanel mainPanel = new JPanel(new BorderLayout(ThemeManager.SPACING_MEDIUM, ThemeManager.SPACING_MEDIUM));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(
                ThemeManager.SPACING_LARGE, ThemeManager.SPACING_LARGE, 
                ThemeManager.SPACING_LARGE, ThemeManager.SPACING_LARGE));
        mainPanel.setBackground(ThemeManager.BACKGROUND_COLOR);
        
        // Create header with title
        JLabel titleLabel = ThemeManager.createTitleLabel("Quiz Results: " + result.getQuizTitle());
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Create score panel
        JPanel scorePanel = new JPanel();
        scorePanel.setLayout(new BoxLayout(scorePanel, BoxLayout.Y_AXIS));
        scorePanel.setBackground(ThemeManager.BACKGROUND_COLOR);
        scorePanel.setBorder(BorderFactory.createEmptyBorder(
                ThemeManager.SPACING_MEDIUM, 0, ThemeManager.SPACING_MEDIUM, 0));
        
        // Add score components to score panel
        scorePanel.add(scoreLabel);
        scorePanel.add(Box.createRigidArea(new Dimension(0, ThemeManager.SPACING_SMALL)));
        scorePanel.add(percentageLabel);
        scorePanel.add(Box.createRigidArea(new Dimension(0, ThemeManager.SPACING_SMALL)));
        scorePanel.add(timeLabel);
        
        // Create scroll pane for questions table
        JScrollPane tableScrollPane = new JScrollPane(questionsTable);
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(ThemeManager.BACKGROUND_COLOR);
        buttonPanel.add(closeButton);
        
        // Add components to main panel
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(scorePanel, BorderLayout.CENTER);
        mainPanel.add(tableScrollPane, BorderLayout.SOUTH);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Set content pane
        setContentPane(mainPanel);
    }
    
    private void addEventListeners() {
        closeButton.addActionListener((ActionEvent e) -> {
            dispose();
        });
    }
    
    private void loadResultData() {
        // Set score labels
        scoreLabel.setText("Score: " + result.getScore() + " / " + result.getMaxScore());
        
        double percentage = result.getPercentageScore();
        percentageLabel.setText(String.format("%.1f%%", percentage));
        
        // Set percentage label color based on score
        if (percentage >= 80) {
            percentageLabel.setForeground(ThemeManager.SUCCESS_COLOR);
        } else if (percentage >= 60) {
            percentageLabel.setForeground(ThemeManager.PRIMARY_COLOR);
        } else if (percentage >= 40) {
            percentageLabel.setForeground(ThemeManager.WARNING_COLOR);
        } else {
            percentageLabel.setForeground(ThemeManager.ERROR_COLOR);
        }
        
        // Format time
        long seconds = result.getDurationInSeconds();
        long minutes = seconds / 60;
        long remainingSeconds = seconds % 60;
        timeLabel.setText(String.format("Time taken: %d:%02d", minutes, remainingSeconds));
        
        // Load questions and answers
        for (QuestionResult questionResult : result.getQuestionResults()) {
            tableModel.addRow(new Object[]{
                questionResult.getQuestionText(),
                formatSelectedOptions(questionResult),
                questionResult.isCorrect(),
                questionResult.isCorrect() ? questionResult.getPoints() : 0
            });
        }
    }
    
    private String formatSelectedOptions(QuestionResult questionResult) {
        // In a real implementation, this would show the actual text of selected options
        // Here we just show the option IDs
        StringBuilder sb = new StringBuilder();
        List<Integer> selectedOptionIds = questionResult.getSelectedOptionIds();
        
        if (selectedOptionIds.isEmpty()) {
            return "No answer";
        }
        
        for (int i = 0; i < selectedOptionIds.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append("Option ").append(selectedOptionIds.get(i));
        }
        
        return sb.toString();
    }
}