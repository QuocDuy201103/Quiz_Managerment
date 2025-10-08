package com.quiz.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Enhanced exam result dialog with animations and visual feedback
 */
public class ExamResultDialog extends JDialog {
    private double score;
    private int totalQuestions;
    private int correctAnswers;
    private int timeUsed;
    
    public ExamResultDialog(JFrame parent, double score, int totalQuestions, int correctAnswers, 
                           int timeUsed, int totalTime) {
        super(parent, "Kết quả thi", true);
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.correctAnswers = correctAnswers;
        this.timeUsed = timeUsed;
        
        initializeComponents();
        setupLayout();
        setupFrame();
        startAnimation();
    }
    
    private void initializeComponents() {
        // Components will be created in setupLayout
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 250, 252));
        
        // Main content panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        mainPanel.setBackground(new Color(248, 250, 252));
        
        // Header with emoji and title
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(new Color(248, 250, 252));
        
        JLabel titleLabel = new JLabel("🎉 Kết quả thi của bạn");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(31, 41, 55));
        headerPanel.add(titleLabel);
        
        // Score display panel
        JPanel scorePanel = createScorePanel();
        
        // Statistics panel
        JPanel statsPanel = createStatsPanel();
        
        // Performance feedback panel
        JPanel feedbackPanel = createFeedbackPanel();
        
        // Buttons panel
        JPanel buttonPanel = createButtonPanel();
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(scorePanel, BorderLayout.CENTER);
        mainPanel.add(statsPanel, BorderLayout.SOUTH);
        
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(mainPanel, BorderLayout.CENTER);
        contentPanel.add(feedbackPanel, BorderLayout.SOUTH);
        
        add(contentPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createScorePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(248, 250, 252));
        panel.setBorder(new EmptyBorder(20, 0, 20, 0));
        
        // Circular score display
        CircularScorePanel scoreDisplay = new CircularScorePanel(score, totalQuestions);
        panel.add(scoreDisplay, BorderLayout.CENTER);
        
        // Score text
        JLabel scoreText = new JLabel(String.format("Điểm: %.1f/10", score));
        scoreText.setFont(new Font("Segoe UI", Font.BOLD, 18));
        scoreText.setHorizontalAlignment(SwingConstants.CENTER);
        scoreText.setForeground(new Color(31, 41, 55));
        panel.add(scoreText, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 15, 15));
        panel.setBackground(new Color(248, 250, 252));
        panel.setBorder(new EmptyBorder(20, 0, 20, 0));
        
        // Correct answers
        JPanel correctPanel = createStatCard("✅ Đúng", correctAnswers + "/" + totalQuestions, 
            new Color(34, 197, 94));
        
        // Accuracy
        double accuracy = totalQuestions > 0 ? (double) correctAnswers / totalQuestions * 100 : 0;
        JPanel accuracyPanel = createStatCard("🎯 Độ chính xác", String.format("%.1f%%", accuracy), 
            new Color(59, 130, 246));
        
        // Time used
        int minutes = timeUsed / 60;
        int seconds = timeUsed % 60;
        JPanel timePanel = createStatCard("⏱️ Thời gian", String.format("%02d:%02d", minutes, seconds), 
            new Color(245, 158, 11));
        
        // Speed
        double avgTimePerQuestion = totalQuestions > 0 ? (double) timeUsed / totalQuestions : 0;
        JPanel speedPanel = createStatCard("⚡ Tốc độ", String.format("%.1fs/câu", avgTimePerQuestion), 
            new Color(168, 85, 247));
        
        panel.add(correctPanel);
        panel.add(accuracyPanel);
        panel.add(timePanel);
        panel.add(speedPanel);
        
        return panel;
    }
    
    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLabel.setForeground(new Color(107, 114, 128));
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        valueLabel.setForeground(color);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createFeedbackPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(new Color(248, 250, 252));
        panel.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        String feedback = getPerformanceFeedback();
        JLabel feedbackLabel = new JLabel("<html><div style='text-align: center; width: 400px;'>" + 
            feedback + "</div></html>");
        feedbackLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        feedbackLabel.setForeground(new Color(75, 85, 99));
        
        panel.add(feedbackLabel);
        return panel;
    }
    
    private String getPerformanceFeedback() {
        double percentage = (double) correctAnswers / totalQuestions * 100;
        
        if (percentage >= 90) {
            return "🌟 Xuất sắc! Bạn đã thể hiện kiến thức tuyệt vời!";
        } else if (percentage >= 80) {
            return "👏 Tốt lắm! Bạn đã làm rất tốt!";
        } else if (percentage >= 70) {
            return "👍 Khá tốt! Hãy tiếp tục cố gắng!";
        } else if (percentage >= 60) {
            return "📚 Cần cải thiện! Hãy ôn tập thêm nhé!";
        } else {
            return "💪 Đừng nản lòng! Hãy học tập chăm chỉ hơn!";
        }
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(new Color(248, 250, 252));
        panel.setBorder(new EmptyBorder(20, 0, 20, 0));
        
        JButton closeButton = new JButton("Đóng");
        closeButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        closeButton.setPreferredSize(new Dimension(100, 35));
        StyleUtil.primary(closeButton);
        StyleUtil.square(closeButton);
        
        closeButton.addActionListener(e -> dispose());
        
        panel.add(closeButton);
        return panel;
    }
    
    private void setupFrame() {
        setSize(500, 600);
        setLocationRelativeTo(getParent());
        setResizable(false);
    }
    
    private void startAnimation() {
        // Simple fade-in animation
        setOpacity(0.0f);
        Timer fadeTimer = new Timer(50, new ActionListener() {
            private float opacity = 0.0f;
            
            @Override
            public void actionPerformed(ActionEvent e) {
                opacity += 0.1f;
                if (opacity >= 1.0f) {
                    opacity = 1.0f;
                    ((Timer) e.getSource()).stop();
                }
                setOpacity(opacity);
            }
        });
        fadeTimer.start();
    }
    
    /**
     * Circular score display component
     */
    private class CircularScorePanel extends JPanel {
        private int animatedScore = 0;
        
        public CircularScorePanel(double score, int totalQuestions) {
            setPreferredSize(new Dimension(200, 200));
            setOpaque(false);
            
            // Start score animation
            Timer scoreTimer = new Timer(50, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (animatedScore < score) {
                        animatedScore++;
                        repaint();
                    } else {
                        ((Timer) e.getSource()).stop();
                    }
                }
            });
            scoreTimer.start();
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int size = Math.min(getWidth(), getHeight()) - 40;
            int x = (getWidth() - size) / 2;
            int y = (getHeight() - size) / 2;
            
            // Background circle
            g2d.setColor(new Color(240, 240, 240));
            g2d.fillOval(x, y, size, size);
            
            // Progress arc
            double progress = animatedScore / 10.0;
            int arcAngle = (int) (360 * progress);
            
            // Color based on score
            if (animatedScore >= 8) {
                g2d.setColor(new Color(34, 197, 94)); // Green
            } else if (animatedScore >= 6) {
                g2d.setColor(new Color(245, 158, 11)); // Orange
            } else {
                g2d.setColor(new Color(239, 68, 68)); // Red
            }
            
            g2d.setStroke(new BasicStroke(12, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2d.drawArc(x + 6, y + 6, size - 12, size - 12, 90, arcAngle);
            
            // Score text
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 32));
            String scoreText = String.valueOf(animatedScore);
            FontMetrics fm = g2d.getFontMetrics();
            int textX = x + (size - fm.stringWidth(scoreText)) / 2;
            int textY = y + size / 2 + fm.getAscent() / 2;
            g2d.drawString(scoreText, textX, textY);
            
            g2d.dispose();
        }
    }
}
