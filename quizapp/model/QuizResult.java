package com.quizapp.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the result of a user's quiz attempt.
 */
public class QuizResult {
    private int id;
    private int userId;
    private int quizId;
    private String quizTitle;
    private int score;
    private int maxScore;
    private LocalDateTime completedAt;
    private long durationInSeconds;
    private List<QuestionResult> questionResults;
    
    // Constructor for new quiz results (no id yet)
    public QuizResult(int userId, int quizId, String quizTitle) {
        this.userId = userId;
        this.quizId = quizId;
        this.quizTitle = quizTitle;
        this.score = 0;
        this.maxScore = 0;
        this.completedAt = LocalDateTime.now();
        this.questionResults = new ArrayList<>();
    }
    
    // Constructor for existing quiz results (with id)
    public QuizResult(int id, int userId, int quizId, String quizTitle, int score, int maxScore, 
                      LocalDateTime completedAt, long durationInSeconds) {
        this.id = id;
        this.userId = userId;
        this.quizId = quizId;
        this.quizTitle = quizTitle;
        this.score = score;
        this.maxScore = maxScore;
        this.completedAt = completedAt;
        this.durationInSeconds = durationInSeconds;
        this.questionResults = new ArrayList<>();
    }
    
    // Getters and setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public int getQuizId() {
        return quizId;
    }
    
    public void setQuizId(int quizId) {
        this.quizId = quizId;
    }
    
    public String getQuizTitle() {
        return quizTitle;
    }
    
    public void setQuizTitle(String quizTitle) {
        this.quizTitle = quizTitle;
    }
    
    public int getScore() {
        return score;
    }
    
    public void setScore(int score) {
        this.score = score;
    }
    
    public int getMaxScore() {
        return maxScore;
    }
    
    public void setMaxScore(int maxScore) {
        this.maxScore = maxScore;
    }
    
    public LocalDateTime getCompletedAt() {
        return completedAt;
    }
    
    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
    
    public long getDurationInSeconds() {
        return durationInSeconds;
    }
    
    public void setDurationInSeconds(long durationInSeconds) {
        this.durationInSeconds = durationInSeconds;
    }
    
    public List<QuestionResult> getQuestionResults() {
        return questionResults;
    }
    
    public void setQuestionResults(List<QuestionResult> questionResults) {
        this.questionResults = questionResults;
    }
    
    public void addQuestionResult(QuestionResult questionResult) {
        this.questionResults.add(questionResult);
        
        // Update score and max score
        if (questionResult.isCorrect()) {
            this.score += questionResult.getPoints();
        }
        this.maxScore += questionResult.getPoints();
    }
    
    public double getPercentageScore() {
        if (maxScore == 0) {
            return 0;
        }
        return (double) score / maxScore * 100;
    }
    
    @Override
    public String toString() {
        return "QuizResult{" +
                "id=" + id +
                ", userId=" + userId +
                ", quizId=" + quizId +
                ", quizTitle='" + quizTitle + '\'' +
                ", score=" + score +
                ", maxScore=" + maxScore +
                ", completedAt=" + completedAt +
                ", durationInSeconds=" + durationInSeconds +
                '}';
    }
    
    /**
     * Represents the result of answering a single question within a quiz.
     */
    public static class QuestionResult {
        private int id;
        private int quizResultId;
        private int questionId;
        private String questionText;
        private boolean isCorrect;
        private List<Integer> selectedOptionIds;
        private int points;
        
        // Constructor for new question results (no id yet)
        public QuestionResult(int quizResultId, int questionId, String questionText, boolean isCorrect, 
                             List<Integer> selectedOptionIds, int points) {
            this.quizResultId = quizResultId;
            this.questionId = questionId;
            this.questionText = questionText;
            this.isCorrect = isCorrect;
            this.selectedOptionIds = selectedOptionIds;
            this.points = points;
        }
        
        // Constructor for existing question results (with id)
        public QuestionResult(int id, int quizResultId, int questionId, String questionText, boolean isCorrect, 
                             List<Integer> selectedOptionIds, int points) {
            this.id = id;
            this.quizResultId = quizResultId;
            this.questionId = questionId;
            this.questionText = questionText;
            this.isCorrect = isCorrect;
            this.selectedOptionIds = selectedOptionIds;
            this.points = points;
        }
        
        // Getters and setters
        public int getId() {
            return id;
        }
        
        public void setId(int id) {
            this.id = id;
        }
        
        public int getQuizResultId() {
            return quizResultId;
        }
        
        public void setQuizResultId(int quizResultId) {
            this.quizResultId = quizResultId;
        }
        
        public int getQuestionId() {
            return questionId;
        }
        
        public void setQuestionId(int questionId) {
            this.questionId = questionId;
        }
        
        public String getQuestionText() {
            return questionText;
        }
        
        public void setQuestionText(String questionText) {
            this.questionText = questionText;
        }
        
        public boolean isCorrect() {
            return isCorrect;
        }
        
        public void setCorrect(boolean correct) {
            isCorrect = correct;
        }
        
        public List<Integer> getSelectedOptionIds() {
            return selectedOptionIds;
        }
        
        public void setSelectedOptionIds(List<Integer> selectedOptionIds) {
            this.selectedOptionIds = selectedOptionIds;
        }
        
        public int getPoints() {
            return points;
        }
        
        public void setPoints(int points) {
            this.points = points;
        }
        
        @Override
        public String toString() {
            return "QuestionResult{" +
                    "id=" + id +
                    ", quizResultId=" + quizResultId +
                    ", questionId=" + questionId +
                    ", questionText='" + questionText + '\'' +
                    ", isCorrect=" + isCorrect +
                    ", points=" + points +
                    '}';
        }
    }
}