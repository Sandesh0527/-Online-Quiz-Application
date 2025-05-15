package com.quizapp.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a quiz in the system.
 */
public class Quiz {
    private int id;
    private String title;
    private String description;
    private int creatorId;
    private String creatorName;
    private int timeLimit; // in minutes, 0 means no time limit
    private List<Question> questions;
    
    // Constructor for new quizzes (no id yet)
    public Quiz(String title, String description, int creatorId, int timeLimit) {
        this.title = title;
        this.description = description;
        this.creatorId = creatorId;
        this.timeLimit = timeLimit;
        this.questions = new ArrayList<>();
    }
    
    // Constructor for existing quizzes (with id)
    public Quiz(int id, String title, String description, int creatorId, String creatorName, int timeLimit) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.creatorId = creatorId;
        this.creatorName = creatorName;
        this.timeLimit = timeLimit;
        this.questions = new ArrayList<>();
    }
    
    // Getters and setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public int getCreatorId() {
        return creatorId;
    }
    
    public void setCreatorId(int creatorId) {
        this.creatorId = creatorId;
    }
    
    public String getCreatorName() {
        return creatorName;
    }
    
    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }
    
    public int getTimeLimit() {
        return timeLimit;
    }
    
    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }
    
    public List<Question> getQuestions() {
        return questions;
    }
    
    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }
    
    public void addQuestion(Question question) {
        this.questions.add(question);
    }
    
    public int getQuestionCount() {
        return questions.size();
    }
    
    @Override
    public String toString() {
        return "Quiz{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", creatorId=" + creatorId +
                ", timeLimit=" + timeLimit +
                ", questionCount=" + questions.size() +
                '}';
    }
}