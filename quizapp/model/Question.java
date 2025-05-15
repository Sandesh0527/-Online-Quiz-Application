package com.quizapp.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a quiz question with multiple-choice options.
 */
public class Question {
    private int id;
    private int quizId;
    private String text;
    private List<Option> options;
    private int points;
    
    // Constructor for new questions (no id yet)
    public Question(int quizId, String text, int points) {
        this.quizId = quizId;
        this.text = text;
        this.points = points;
        this.options = new ArrayList<>();
    }
    
    // Constructor for existing questions (with id)
    public Question(int id, int quizId, String text, int points) {
        this.id = id;
        this.quizId = quizId;
        this.text = text;
        this.points = points;
        this.options = new ArrayList<>();
    }
    
    // Getters and setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getQuizId() {
        return quizId;
    }
    
    public void setQuizId(int quizId) {
        this.quizId = quizId;
    }
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public List<Option> getOptions() {
        return options;
    }
    
    public void setOptions(List<Option> options) {
        this.options = options;
    }
    
    public void addOption(Option option) {
        this.options.add(option);
    }
    
    public int getPoints() {
        return points;
    }
    
    public void setPoints(int points) {
        this.points = points;
    }
    
    /**
     * Gets the correct options for this question.
     */
    public List<Option> getCorrectOptions() {
        List<Option> correctOptions = new ArrayList<>();
        for (Option option : options) {
            if (option.isCorrect()) {
                correctOptions.add(option);
            }
        }
        return correctOptions;
    }
    
    /**
     * Checks if the given options are correct.
     */
    public boolean checkAnswer(List<Integer> selectedOptionIds) {
        // Get all correct option IDs
        List<Integer> correctOptionIds = new ArrayList<>();
        for (Option option : options) {
            if (option.isCorrect()) {
                correctOptionIds.add(option.getId());
            }
        }
        
        // Check if the selected options match the correct options
        return selectedOptionIds.containsAll(correctOptionIds) && 
               correctOptionIds.containsAll(selectedOptionIds);
    }
    
    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", quizId=" + quizId +
                ", text='" + text + '\'' +
                ", points=" + points +
                ", optionsCount=" + options.size() +
                '}';
    }
    
    /**
     * Represents an option for a multiple-choice question.
     */
    public static class Option {
        private int id;
        private int questionId;
        private String text;
        private boolean isCorrect;
        
        // Constructor for new options (no id yet)
        public Option(int questionId, String text, boolean isCorrect) {
            this.questionId = questionId;
            this.text = text;
            this.isCorrect = isCorrect;
        }
        
        // Constructor for existing options (with id)
        public Option(int id, int questionId, String text, boolean isCorrect) {
            this.id = id;
            this.questionId = questionId;
            this.text = text;
            this.isCorrect = isCorrect;
        }
        
        // Getters and setters
        public int getId() {
            return id;
        }
        
        public void setId(int id) {
            this.id = id;
        }
        
        public int getQuestionId() {
            return questionId;
        }
        
        public void setQuestionId(int questionId) {
            this.questionId = questionId;
        }
        
        public String getText() {
            return text;
        }
        
        public void setText(String text) {
            this.text = text;
        }
        
        public boolean isCorrect() {
            return isCorrect;
        }
        
        public void setCorrect(boolean correct) {
            isCorrect = correct;
        }
        
        @Override
        public String toString() {
            return "Option{" +
                    "id=" + id +
                    ", questionId=" + questionId +
                    ", text='" + text + '\'' +
                    ", isCorrect=" + isCorrect +
                    '}';
        }
    }
}