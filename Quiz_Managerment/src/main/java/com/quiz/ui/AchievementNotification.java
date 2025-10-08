package com.quiz.ui;

import com.quiz.model.Achievement;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Animated achievement notification
 */
public class AchievementNotification extends JWindow {
    private Achievement achievement;
    private Timer slideTimer;
    private Timer fadeTimer;
    private int slidePosition = -300;
    private float opacity = 0.0f;
    
    public AchievementNotification(Achievement achievement) {
        this.achievement = achievement;
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
        setBackground(new Color(0, 0, 0, 0)); // Transparent background
        
        // Main panel with gradient background
        JPanel mainPanel = new GradientPanel();
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(245, 158, 11), 2),
            new EmptyBorder(15, 20, 15, 20)
        ));
        
        // Achievement icon and text
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        
        JLabel iconLabel = new JLabel(achievement.getIcon());
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 32));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel(achievement.getName());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel descLabel = new JLabel(achievement.getDescription());
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descLabel.setForeground(new Color(255, 255, 255, 200));
        
        textPanel.add(titleLabel, BorderLayout.NORTH);
        textPanel.add(descLabel, BorderLayout.CENTER);
        
        contentPanel.add(iconLabel, BorderLayout.WEST);
        contentPanel.add(textPanel, BorderLayout.CENTER);
        
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private void setupFrame() {
        setSize(350, 80);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);
        setOpacity(0.0f);
    }
    
    private void startAnimation() {
        // Slide in from right
        slideTimer = new Timer(20, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                slidePosition += 15;
                if (slidePosition >= 50) {
                    slidePosition = 50;
                    ((Timer) e.getSource()).stop();
                    
                    // Start fade in
                    startFadeIn();
                }
                setLocation(slidePosition, 50);
            }
        });
        slideTimer.start();
    }
    
    private void startFadeIn() {
        fadeTimer = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                opacity += 0.1f;
                if (opacity >= 1.0f) {
                    opacity = 1.0f;
                    ((Timer) e.getSource()).stop();
                    
                    // Auto-hide after 3 seconds
                    Timer hideTimer = new Timer(3000, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            hideNotification();
                        }
                    });
                    hideTimer.setRepeats(false);
                    hideTimer.start();
                }
                setOpacity(opacity);
            }
        });
        fadeTimer.start();
    }
    
    private void hideNotification() {
        Timer hideTimer = new Timer(20, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                slidePosition -= 15;
                if (slidePosition <= -350) {
                    ((Timer) e.getSource()).stop();
                    dispose();
                }
                setLocation(slidePosition, 50);
            }
        });
        hideTimer.start();
    }
    
    /**
     * Gradient background panel
     */
    private class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Create gradient background
            GradientPaint gradient = new GradientPaint(
                0, 0, new Color(245, 158, 11),
                getWidth(), 0, new Color(251, 191, 36)
            );
            g2d.setPaint(gradient);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            
            g2d.dispose();
        }
    }
    
    public static void showAchievement(Achievement achievement) {
        SwingUtilities.invokeLater(() -> {
            new AchievementNotification(achievement).setVisible(true);
        });
    }
}
