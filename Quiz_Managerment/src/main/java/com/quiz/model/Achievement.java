package com.quiz.model;

import java.time.LocalDateTime;

/**
 * Achievement model for gamification
 */
public class Achievement {
    private int id;
    private String name;
    private String description;
    private String icon;
    private String category;
    private int points;
    private LocalDateTime unlockedAt;
    private boolean isUnlocked;
    
    public Achievement() {}
    
    public Achievement(String name, String description, String icon, String category, int points) {
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.category = category;
        this.points = points;
        this.isUnlocked = false;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }
    
    public LocalDateTime getUnlockedAt() { return unlockedAt; }
    public void setUnlockedAt(LocalDateTime unlockedAt) { this.unlockedAt = unlockedAt; }
    
    public boolean isUnlocked() { return isUnlocked; }
    public void setUnlocked(boolean unlocked) { isUnlocked = unlocked; }
    
    @Override
    public String toString() {
        return name;
    }
}
