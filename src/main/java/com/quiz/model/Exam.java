package com.quiz.model;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Model class cho bảng Exams
 */
public class Exam {
    private int id;
    private String title;
    private int duration; // phút
    private int subjectId;
    private int createdBy;
    private LocalDateTime createdAt;
    
    // Related objects
    private Subject subject;
    private User createdByUser;
    private List<Question> questions;

    public Exam() {}

    public Exam(String title, int duration, int subjectId, int createdBy) {
        this.title = title;
        this.duration = duration;
        this.subjectId = subjectId;
        this.createdBy = createdBy;
    }

    // Getters and Setters
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

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public User getCreatedByUser() {
        return createdByUser;
    }

    public void setCreatedByUser(User createdByUser) {
        this.createdByUser = createdByUser;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    @Override
    public String toString() {
        return title;
    }
}
