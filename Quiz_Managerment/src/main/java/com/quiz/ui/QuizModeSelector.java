package com.quiz.ui;

import com.quiz.model.Exam;
import com.quiz.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Fun quiz mode selector with different game styles
 */
public class QuizModeSelector extends JDialog {
    private QuizMode selectedMode;
    
    public enum QuizMode {
        EASY("Dễ", "Có gợi ý, mỗi câu 1 phút để chọn đáp án", "/images/one.png"),
        MEDIUM("Trung bình", "Câu hỏi ngẫu nhiên, mỗi câu 1 phút, không có gợi ý", "/images/two.png"),
        HARD("Khó", "Không có gợi ý, mỗi câu 10 giây để chọn đáp án", "/images/three.png"),
        QUIZ_GAME("Trò chơi", "Ghép câu hỏi với đáp án đúng", "/images/joystick.png");
        
        private final String title;
        private final String description;
        private final String iconPath;
        
        QuizMode(String title, String description, String iconPath) {
            this.title = title;
            this.description = description;
            this.iconPath = iconPath;
        }
        
        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public String getIconPath() { return iconPath; }
    }
    
    public QuizModeSelector(JFrame parent, Exam exam, User currentUser) {
        super(parent, "Chọn chế độ thi", true);
        
        initializeComponents();
        setupLayout();
        setupFrame();
    }
    
    private void initializeComponents() {
        // Components will be created in setupLayout
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 250, 252));
        
        // Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(new Color(248, 250, 252));
        headerPanel.setBorder(new EmptyBorder(20, 20, 10, 20));
        
        JLabel titleLabel = new JLabel("Chọn chế độ thi");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(31, 41, 55));
        headerPanel.add(titleLabel);
        
        // JLabel subtitleLabel = new JLabel("Chọn chế độ thi phù hợp với bạn");
        // subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        // subtitleLabel.setForeground(new Color(107, 114, 128));
        // headerPanel.add(subtitleLabel);
        
        // Mode selection panel
        JPanel modePanel = new JPanel(new GridLayout(0, 1, 10, 10));
        modePanel.setBackground(new Color(248, 250, 252));
        modePanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        for (QuizMode mode : QuizMode.values()) {
            JPanel modeCard = createModeCard(mode);
            modePanel.add(modeCard);
        }
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(new Color(248, 250, 252));
        buttonPanel.setBorder(new EmptyBorder(10, 20, 20, 20));
        
        JButton cancelButton = new JButton("Hủy");
        cancelButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cancelButton.setPreferredSize(new Dimension(100, 35));
        StyleUtil.secondary(cancelButton);
        StyleUtil.square(cancelButton);
        cancelButton.addActionListener(e -> {
            selectedMode = null;
            dispose();
        });
        
        buttonPanel.add(cancelButton);
        
        add(headerPanel, BorderLayout.NORTH);
        add(new JScrollPane(modePanel), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createModeCard(QuizMode mode) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
            new EmptyBorder(20, 20, 20, 20)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                card.setBackground(new Color(240, 248, 255));
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(59, 130, 246), 2),
                    new EmptyBorder(20, 20, 20, 20)
                ));
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                card.setBackground(Color.WHITE);
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
                    new EmptyBorder(20, 20, 20, 20)
                ));
            }
            
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                selectMode(mode);
            }
        });
        
        // Icon and Title
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titlePanel.setBackground(Color.WHITE);
        
        // Load and add icon
        try {
            ImageIcon icon = com.quiz.ui.IconUtil.load(mode.getIconPath(), 24, 24);
            JLabel iconLabel = new JLabel(icon);
            titlePanel.add(iconLabel);
        } catch (Exception e) {
            // Fallback to emoji if icon fails to load
            JLabel fallbackIcon = new JLabel("🎯");
            fallbackIcon.setFont(new Font("Segoe UI", Font.PLAIN, 20));
            titlePanel.add(fallbackIcon);
        }
        
        JLabel titleLabel = new JLabel(mode.getTitle());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(new Color(31, 41, 55));
        titlePanel.add(titleLabel);
        
        // Description
        JLabel descLabel = new JLabel(mode.getDescription());
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descLabel.setForeground(new Color(107, 114, 128));
        
        // Select button
        JButton selectButton = new JButton("Chọn");
        selectButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        selectButton.setPreferredSize(new Dimension(80, 30));
        StyleUtil.primary(selectButton);
        StyleUtil.square(selectButton);
        selectButton.addActionListener(e -> selectMode(mode));
        
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(card.getBackground());
        leftPanel.add(titlePanel, BorderLayout.NORTH);
        leftPanel.add(descLabel, BorderLayout.CENTER);
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setBackground(card.getBackground());
        rightPanel.add(selectButton);
        
        card.add(leftPanel, BorderLayout.CENTER);
        card.add(rightPanel, BorderLayout.EAST);
        
        return card;
    }
    
    private void selectMode(QuizMode mode) {
        selectedMode = mode;
        
        // Show confirmation dialog
        int option = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc chắn muốn bắt đầu thi với chế độ '" + mode.getTitle() + "'?",
            "Xác nhận",
            JOptionPane.YES_NO_OPTION);
        
        if (option == JOptionPane.YES_OPTION) {
            dispose();
        }
    }
    
    private void setupFrame() {
        setSize(600, 500);
        setLocationRelativeTo(getParent());
        setResizable(false);
    }
    
    public QuizMode getSelectedMode() {
        return selectedMode;
    }
    
    public static QuizMode showModeSelector(JFrame parent, Exam exam, User currentUser) {
        QuizModeSelector selector = new QuizModeSelector(parent, exam, currentUser);
        selector.setVisible(true);
        return selector.getSelectedMode();
    }
}
