package com.quiz.model;

/**
 * Model class cho bảng Topics
 */
public class Topic {
    private int id;
    private String name;
    private int subjectId;
    private Subject subject;

    public Topic() {}

    public Topic(String name, int subjectId) {
        this.name = name;
        this.subjectId = subjectId;
    }

    public Topic(int id, String name, int subjectId) {
        this.id = id;
        this.name = name;
        this.subjectId = subjectId;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    @Override
    public String toString() {
        return name;
    }
}
