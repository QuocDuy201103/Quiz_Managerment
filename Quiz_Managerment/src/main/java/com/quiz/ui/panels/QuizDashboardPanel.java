package com.quiz.ui.panels;

import com.quiz.model.Achievement;
import com.quiz.model.User;
import com.quiz.ui.AchievementNotification;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Fun quiz dashboard with statistics and achievements
 */
public class QuizDashboardPanel extends JPanel {
    private User currentUser;
    private List<Achievement> achievements;
    
    public QuizDashboardPanel(User currentUser) {
        this.currentUser = currentUser;
        this.achievements = new ArrayList<>();
        
        initializeAchievements();
        setupLayout();
        loadUserStats();
    }
    
    private void initializeAchievements() {
        achievements.add(new Achievement("🎯 First Quiz", "Hoàn thành bài thi đầu tiên", "🎯", "Milestone", 10));
        achievements.add(new Achievement("🔥 Hot Streak", "Trả lời đúng 5 câu liên tiếp", "🔥", "Performance", 20));
        achievements.add(new Achievement("⚡ Speed Demon", "Hoàn thành bài thi trong thời gian ngắn", "⚡", "Speed", 15));
        achievements.add(new Achievement("🏆 Perfect Score", "Đạt điểm tuyệt đối 10/10", "🏆", "Performance", 50));
        achievements.add(new Achievement("📚 Knowledge Seeker", "Hoàn thành 10 bài thi", "📚", "Milestone", 30));
        achievements.add(new Achievement("🎮 Arcade Master", "Hoàn thành bài thi ở chế độ Arcade", "🎮", "Mode", 25));
        achievements.add(new Achievement("💪 Persistence", "Hoàn thành 5 bài thi liên tiếp", "💪", "Consistency", 35));
        achievements.add(new Achievement("🌟 Rising Star", "Cải thiện điểm số qua 3 bài thi liên tiếp", "🌟", "Improvement", 40));
    }
    
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 250, 252));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Header
        JPanel headerPanel = createHeaderPanel();
        
        // Main content
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(new Color(248, 250, 252));
        
        // Stats panel
        JPanel statsPanel = createStatsPanel();
        
        // Achievements panel
        JPanel achievementsPanel = createAchievementsPanel();
        
        // Recent activity panel
        JPanel activityPanel = createActivityPanel();
        
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(new Color(248, 250, 252));
        leftPanel.add(statsPanel, BorderLayout.NORTH);
        leftPanel.add(achievementsPanel, BorderLayout.CENTER);
        
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(new Color(248, 250, 252));
        rightPanel.add(activityPanel, BorderLayout.CENTER);
        
        mainContent.add(leftPanel, BorderLayout.WEST);
        mainContent.add(rightPanel, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
        add(mainContent, BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(248, 250, 252));
        
        JLabel welcomeLabel = new JLabel("🎉 Chào mừng trở lại, " + currentUser.getUsername() + "!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeLabel.setForeground(new Color(31, 41, 55));
        
        JLabel subtitleLabel = new JLabel("Hãy tiếp tục hành trình học tập của bạn!");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(107, 114, 128));
        
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(new Color(248, 250, 252));
        leftPanel.add(welcomeLabel, BorderLayout.NORTH);
        leftPanel.add(subtitleLabel, BorderLayout.CENTER);
        
        // Quick stats
        JPanel quickStatsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        quickStatsPanel.setBackground(new Color(248, 250, 252));
        
        JLabel levelLabel = new JLabel("Level: 1");
        levelLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        levelLabel.setForeground(new Color(59, 130, 246));
        
        JLabel xpLabel = new JLabel("XP: 0");
        xpLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        xpLabel.setForeground(new Color(34, 197, 94));
        
        quickStatsPanel.add(levelLabel);
        quickStatsPanel.add(Box.createHorizontalStrut(20));
        quickStatsPanel.add(xpLabel);
        
        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(quickStatsPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 15, 15));
        panel.setBackground(new Color(248, 250, 252));
        panel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        // Total quizzes
        JPanel totalPanel = createStatCard("📊 Tổng bài thi", "0", new Color(59, 130, 246));
        
        // Average score
        JPanel avgPanel = createStatCard("📈 Điểm trung bình", "0.0", new Color(34, 197, 94));
        
        // Best score
        JPanel bestPanel = createStatCard("🏆 Điểm cao nhất", "0.0", new Color(245, 158, 11));
        
        // Study streak
        JPanel streakPanel = createStatCard("🔥 Chuỗi học tập", "0 ngày", new Color(239, 68, 68));
        
        panel.add(totalPanel);
        panel.add(avgPanel);
        panel.add(bestPanel);
        panel.add(streakPanel);
        
        return panel;
    }
    
    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLabel.setForeground(new Color(107, 114, 128));
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        valueLabel.setForeground(color);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createAchievementsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(248, 250, 252));
        panel.setBorder(new EmptyBorder(0, 0, 0, 10));
        
        JLabel titleLabel = new JLabel("🏆 Thành tích");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(31, 41, 55));
        
        JPanel achievementsGrid = new JPanel(new GridLayout(0, 2, 10, 10));
        achievementsGrid.setBackground(new Color(248, 250, 252));
        
        for (Achievement achievement : achievements) {
            JPanel achievementCard = createAchievementCard(achievement);
            achievementsGrid.add(achievementCard);
        }
        
        JScrollPane scrollPane = new JScrollPane(achievementsGrid);
        scrollPane.setBackground(new Color(248, 250, 252));
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createAchievementCard(Achievement achievement) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(achievement.isUnlocked() ? Color.WHITE : new Color(240, 240, 240));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(achievement.isUnlocked() ? new Color(245, 158, 11) : new Color(229, 231, 235), 1),
            new EmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel iconLabel = new JLabel(achievement.getIcon());
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setOpaque(false);
        
        JLabel nameLabel = new JLabel(achievement.getName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        nameLabel.setForeground(achievement.isUnlocked() ? new Color(31, 41, 55) : new Color(156, 163, 175));
        
        JLabel descLabel = new JLabel("<html><div style='width: 120px;'>" + achievement.getDescription() + "</div></html>");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        descLabel.setForeground(achievement.isUnlocked() ? new Color(107, 114, 128) : new Color(156, 163, 175));
        
        textPanel.add(nameLabel, BorderLayout.NORTH);
        textPanel.add(descLabel, BorderLayout.CENTER);
        
        card.add(iconLabel, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createActivityPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel titleLabel = new JLabel("📈 Hoạt động gần đây");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(31, 41, 55));
        
        JPanel activityList = new JPanel();
        activityList.setLayout(new BoxLayout(activityList, BoxLayout.Y_AXIS));
        activityList.setBackground(Color.WHITE);
        
        // Sample activities
        activityList.add(createActivityItem("🎯 Hoàn thành bài thi Java", "2 giờ trước", "8.5/10"));
        activityList.add(createActivityItem("🔥 Đạt chuỗi 5 câu đúng", "1 ngày trước", ""));
        activityList.add(createActivityItem("📚 Hoàn thành bài thi OOP", "2 ngày trước", "7.0/10"));
        activityList.add(createActivityItem("🏆 Mở khóa thành tích mới", "3 ngày trước", ""));
        
        JScrollPane scrollPane = new JScrollPane(activityList);
        scrollPane.setBackground(Color.WHITE);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createActivityItem(String activity, String time, String score) {
        JPanel item = new JPanel(new BorderLayout());
        item.setBackground(Color.WHITE);
        item.setBorder(new EmptyBorder(10, 0, 10, 0));
        
        JLabel activityLabel = new JLabel(activity);
        activityLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        activityLabel.setForeground(new Color(31, 41, 55));
        
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(Color.WHITE);
        
        JLabel timeLabel = new JLabel(time);
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        timeLabel.setForeground(new Color(107, 114, 128));
        
        if (!score.isEmpty()) {
            JLabel scoreLabel = new JLabel(score);
            scoreLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            scoreLabel.setForeground(new Color(34, 197, 94));
            rightPanel.add(scoreLabel, BorderLayout.NORTH);
        }
        
        rightPanel.add(timeLabel, BorderLayout.SOUTH);
        
        item.add(activityLabel, BorderLayout.CENTER);
        item.add(rightPanel, BorderLayout.EAST);
        
        return item;
    }
    
    private void loadUserStats() {
        achievements.get(0).setUnlocked(true);
        achievements.get(1).setUnlocked(true);
        SwingUtilities.invokeLater(() -> {
            AchievementNotification.showAchievement(achievements.get(0));
        });
    }
}
