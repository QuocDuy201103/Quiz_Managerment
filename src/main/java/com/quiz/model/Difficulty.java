package com.quiz.model;

/**
 * Model class cho bảng Difficulties
 */
public class Difficulty {
    private int id;
    private String level;

    public Difficulty() {}

    public Difficulty(int id, String level) {
        this.id = id;
        this.level = level;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return level;
    }
}
