package com.quizapp.ui.quiz;

import com.quizapp.dao.QuizResultDAO;
import com.quizapp.model.Quiz;
import com.quizapp.model.Question;
import com.quizapp.model.Question.Option;
import com.quizapp.model.QuizResult;
import com.quizapp.model.QuizResult.QuestionResult;
import com.quizapp.model.User;
import com.quizapp.util.ThemeManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Frame for taking a quiz.
 */
public class QuizSessionFrame extends JFrame {
    
    private User currentUser;
    private Quiz quiz;
    private QuizResultDAO quizResultDAO;
    
    private JPanel contentPanel;
    private JPanel questionPanel;
    private JLabel questionLabel;
    private JPanel optionsPanel;
    private JPanel navigationPanel;
    private JButton previousButton;
    private JButton nextButton;
    private JLabel timerLabel;
    private JLabel progressLabel;
    
    private List<Question> questions;
    private int currentQuestionIndex;
    private List<List<Integer>> selectedOptionsByQuestion;
    
    private Timer quizTimer;
    private long startTime;
    private long elapsedTimeInSeconds;
    
    /**
     * Constructor for a new quiz session.
     *
     * @param parent the parent frame
     * @param currentUser the current user
     * @param quiz the quiz to take
     */
    public QuizSessionFrame(JFrame parent, User currentUser, Quiz quiz) {
        this.currentUser = currentUser;
        this.quiz = quiz;
        this.quizResultDAO = new QuizResultDAO();
        
        // Set up the frame
        setTitle("Quiz: " + quiz.getTitle());
        setSize(800, 600);
        setLocationRelativeTo(parent);
        setResizable(true);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        // Initialize quiz state
        questions = quiz.getQuestions();
        currentQuestionIndex = 0;
        selectedOptionsByQuestion = new ArrayList<>(questions.size());
        
        // Initialize selected options for each question
        for (int i = 0; i < questions.size(); i++) {
            selectedOptionsByQuestion.add(new ArrayList<>());
        }
        
        // Initialize components
        initializeComponents();
        
        // Set up layout
        setupLayout();
        
        // Add event listeners
        addEventListeners();
        
        // Start quiz timer
        startQuizTimer();
        
        // Show first question
        showCurrentQuestion();
    }
    
    private void initializeComponents() {
        contentPanel = new JPanel(new BorderLayout(ThemeManager.SPACING_MEDIUM, ThemeManager.SPACING_MEDIUM));
        contentPanel.setBackground(ThemeManager.BACKGROUND_COLOR);
        contentPanel.setBorder(new EmptyBorder(
                ThemeManager.SPACING_LARGE, ThemeManager.SPACING_LARGE, 
                ThemeManager.SPACING_LARGE, ThemeManager.SPACING_LARGE));
        
        // Quiz header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ThemeManager.BACKGROUND_COLOR);
        
        JLabel quizTitleLabel = ThemeManager.createTitleLabel(quiz.getTitle());
        headerPanel.add(quizTitleLabel, BorderLayout.WEST);
        
        timerLabel = ThemeManager.createStyledLabel("Time: 00:00");
        timerLabel.setFont(ThemeManager.HEADING_FONT);
        timerLabel.setForeground(ThemeManager.PRIMARY_COLOR);
        headerPanel.add(timerLabel, BorderLayout.EAST);
        
        // Progress label
        progressLabel = ThemeManager.createStyledLabel("Question 1 of " + questions.size());
        progressLabel.setFont(ThemeManager.BODY_FONT);
        progressLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Question panel
        questionPanel = new JPanel(new BorderLayout(ThemeManager.SPACING_MEDIUM, ThemeManager.SPACING_MEDIUM));
        questionPanel.setBackground(Color.WHITE);
        questionPanel.setBorder(BorderFactory.createEmptyBorder(
                ThemeManager.SPACING_LARGE, ThemeManager.SPACING_LARGE, 
                ThemeManager.SPACING_LARGE, ThemeManager.SPACING_LARGE));
        
        questionLabel = new JLabel("");
        questionLabel.setFont(ThemeManager.HEADING_FONT);
        questionPanel.add(questionLabel, BorderLayout.NORTH);
        
        // Options panel
        optionsPanel = new JPanel();
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
        optionsPanel.setBackground(Color.WHITE);
        questionPanel.add(optionsPanel, BorderLayout.CENTER);
        
        // Navigation buttons
        previousButton = ThemeManager.createStyledButton("Previous");
        nextButton = ThemeManager.createStyledButton("Next");
        
        navigationPanel = new JPanel(new BorderLayout());
        navigationPanel.setBackground(ThemeManager.BACKGROUND_COLOR);
        navigationPanel.add(previousButton, BorderLayout.WEST);
        navigationPanel.add(progressLabel, BorderLayout.CENTER);
        navigationPanel.add(nextButton, BorderLayout.EAST);
        
        // Add components to content panel
        contentPanel.add(headerPanel, BorderLayout.NORTH);
        contentPanel.add(questionPanel, BorderLayout.CENTER);
        contentPanel.add(navigationPanel, BorderLayout.SOUTH);
        
        // Set content pane
        setContentPane(contentPanel);
    }
    
    private void setupLayout() {
        // Nothing additional needed here
    }
    
    private void addEventListeners() {
        previousButton.addActionListener((ActionEvent e) -> {
            navigateToPreviousQuestion();
        });
        
        nextButton.addActionListener((ActionEvent e) -> {
            navigateToNextQuestion();
        });
        
        // Confirm before closing
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmQuit();
            }
        });
    }
    
    private void startQuizTimer() {
        startTime = System.currentTimeMillis();
        
        // If quiz has a time limit, show countdown
        if (quiz.getTimeLimit() > 0) {
            final long timeLimit = quiz.getTimeLimit() * 60; // convert to seconds
            
            quizTimer = new Timer();
            quizTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    long currentTime = System.currentTimeMillis();
                    elapsedTimeInSeconds = (currentTime - startTime) / 1000;
                    long remainingSeconds = timeLimit - elapsedTimeInSeconds;
                    
                    if (remainingSeconds <= 0) {
                        // Time's up!
                        SwingUtilities.invokeLater(() -> {
                            quizTimer.cancel();
                            JOptionPane.showMessageDialog(QuizSessionFrame.this,
                                    "Time's up! Your quiz will be submitted now.",
                                    "Time Expired",
                                    JOptionPane.WARNING_MESSAGE);
                            finishQuiz();
                        });
                    } else {
                        // Update timer label
                        long minutes = remainingSeconds / 60;
                        long seconds = remainingSeconds % 60;
                        final String timeString = String.format("Time left: %02d:%02d", minutes, seconds);
                        
                        SwingUtilities.invokeLater(() -> {
                            timerLabel.setText(timeString);
                            
                            // Change color when time is running low
                            if (remainingSeconds < 60) {
                                timerLabel.setForeground(ThemeManager.ERROR_COLOR);
                            } else if (remainingSeconds < 180) {
                                timerLabel.setForeground(ThemeManager.WARNING_COLOR);
                            }
                        });
                    }
                }
            }, 0, 1000); // Update every second
        } else {
            // No time limit, show elapsed time
            quizTimer = new Timer();
            quizTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    long currentTime = System.currentTimeMillis();
                    elapsedTimeInSeconds = (currentTime - startTime) / 1000;
                    
                    // Update timer label
                    long minutes = elapsedTimeInSeconds / 60;
                    long seconds = elapsedTimeInSeconds % 60;
                    final String timeString = String.format("Time: %02d:%02d", minutes, seconds);
                    
                    SwingUtilities.invokeLater(() -> {
                        timerLabel.setText(timeString);
                    });
                }
            }, 0, 1000); // Update every second
        }
    }
    
    private void showCurrentQuestion() {
        Question currentQuestion = questions.get(currentQuestionIndex);
        
        // Update question text
        questionLabel.setText((currentQuestionIndex + 1) + ". " + currentQuestion.getText() + 
                " (" + currentQuestion.getPoints() + " points)");
        
        // Clear options panel
        optionsPanel.removeAll();
        
        // Get selected options for this question
        List<Integer> selectedOptions = selectedOptionsByQuestion.get(currentQuestionIndex);
        
        // Multiple selection needed for multiple correct answers
        boolean multipleCorrectAnswers = false;
        int correctCount = 0;
        for (Option option : currentQuestion.getOptions()) {
            if (option.isCorrect()) {
                correctCount++;
            }
        }
        multipleCorrectAnswers = correctCount > 1;
        
        // Create option components
        ButtonGroup singleSelectionGroup = new ButtonGroup();
        List<JToggleButton> toggleButtons = new ArrayList<>();
        
        for (Option option : currentQuestion.getOptions()) {
            JToggleButton optionButton;
            
            if (multipleCorrectAnswers) {
                // Checkbox for multiple selection
                JCheckBox checkBox = new JCheckBox(option.getText());
                checkBox.setFont(ThemeManager.BODY_FONT);
                checkBox.setSelected(selectedOptions.contains(option.getId()));
                
                // Add action listener to update selected options
                checkBox.addActionListener(e -> {
                    if (checkBox.isSelected()) {
                        if (!selectedOptions.contains(option.getId())) {
                            selectedOptions.add(option.getId());
                        }
                    } else {
                        selectedOptions.remove(Integer.valueOf(option.getId()));
                    }
                });
                
                optionButton = checkBox;
            } else {
                // Radio button for single selection
                JRadioButton radioButton = new JRadioButton(option.getText());
                radioButton.setFont(ThemeManager.BODY_FONT);
                radioButton.setSelected(selectedOptions.contains(option.getId()));
                singleSelectionGroup.add(radioButton);
                
                // Add action listener to update selected options
                radioButton.addActionListener(e -> {
                    selectedOptions.clear();
                    selectedOptions.add(option.getId());
                });
                
                optionButton = radioButton;
            }
            
            // Style the option button
            optionButton.setBackground(Color.WHITE);
            optionButton.setBorder(BorderFactory.createEmptyBorder(
                    ThemeManager.SPACING_SMALL, ThemeManager.SPACING_SMALL, 
                    ThemeManager.SPACING_SMALL, ThemeManager.SPACING_SMALL));
            optionButton.setFocusPainted(false);
            
            toggleButtons.add(optionButton);
            
            // Add to options panel
            optionsPanel.add(optionButton);
            optionsPanel.add(Box.createRigidArea(new Dimension(0, ThemeManager.SPACING_SMALL)));
        }
        
        // Update navigation buttons state
        previousButton.setEnabled(currentQuestionIndex > 0);
        boolean isLastQuestion = currentQuestionIndex == questions.size() - 1;
        nextButton.setText(isLastQuestion ? "Finish Quiz" : "Next");
        
        // Update progress label
        progressLabel.setText("Question " + (currentQuestionIndex + 1) + " of " + questions.size());
        
        // Repaint components
        questionPanel.revalidate();
        questionPanel.repaint();
    }
    
    private void navigateToPreviousQuestion() {
        if (currentQuestionIndex > 0) {
            currentQuestionIndex--;
            showCurrentQuestion();
        }
    }
    
    private void navigateToNextQuestion() {
        if (currentQuestionIndex < questions.size() - 1) {
            currentQuestionIndex++;
            showCurrentQuestion();
        } else {
            // On last question, finish the quiz
            confirmFinish();
        }
    }
    
    private void confirmFinish() {
        // Check if all questions have been answered
        boolean allAnswered = true;
        for (List<Integer> selectedOptions : selectedOptionsByQuestion) {
            if (selectedOptions.isEmpty()) {
                allAnswered = false;
                break;
            }
        }
        
        String message = allAnswered ?
                "Are you sure you want to finish and submit your quiz?" :
                "You have not answered all questions. Are you sure you want to finish and submit your quiz?";
        
        int choice = JOptionPane.showConfirmDialog(this,
                message,
                "Confirm Submission",
                JOptionPane.YES_NO_OPTION);
        
        if (choice == JOptionPane.YES_OPTION) {
            finishQuiz();
        }
    }
    
    private void confirmQuit() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to quit? Your progress will be lost.",
                "Confirm Quit",
                JOptionPane.YES_NO_OPTION);
        
        if (choice == JOptionPane.YES_OPTION) {
            // Cancel timer
            if (quizTimer != null) {
                quizTimer.cancel();
            }
            dispose();
        }
    }
    
    private void finishQuiz() {
        // Cancel timer
        if (quizTimer != null) {
            quizTimer.cancel();
        }
        
        // Calculate results
        QuizResult result = new QuizResult(currentUser.getId(), quiz.getId(), quiz.getTitle());
        result.setDurationInSeconds(elapsedTimeInSeconds);
        
        int totalPoints = 0;
        int earnedPoints = 0;
        
        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            List<Integer> selectedOptions = selectedOptionsByQuestion.get(i);
            
            boolean isCorrect = question.checkAnswer(selectedOptions);
            
            // Create question result
            QuestionResult questionResult = new QuestionResult(
                    0, // will be set when saved
                    question.getId(),
                    question.getText(),
                    isCorrect,
                    selectedOptions,
                    question.getPoints()
            );
            
            result.addQuestionResult(questionResult);
            
            totalPoints += question.getPoints();
            if (isCorrect) {
                earnedPoints += question.getPoints();
            }
        }
        
        // Set final score
        result.setScore(earnedPoints);
        result.setMaxScore(totalPoints);
        
        try {
            // Save result to database
            result = quizResultDAO.saveQuizResult(result);
            
            // Show results
            showQuizResults(result);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error saving quiz result: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            dispose();
        }
    }
    
    private void showQuizResults(QuizResult result) {
        // Create results dialog
        QuizResultDialog resultDialog = new QuizResultDialog(this, result);
        resultDialog.setVisible(true);
        
        // Close quiz frame when results dialog is closed
        dispose();
    }
}