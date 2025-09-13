package com.quiz.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Dialog thông báo lỗi đăng ký
 */
public class RegisterErrorDialog extends JDialog {
    private String message;
    
    public RegisterErrorDialog(JFrame parent, String message) {
        super(parent, "Lỗi đăng ký", true);
        this.message = message;
        initializeComponents();
        setupLayout();
        setupFrame();
    }
    
    private void initializeComponents() {
        // Components sẽ được tạo trong setupLayout
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        mainPanel.setBackground(Color.WHITE);
        
        // Icon lỗi
        JLabel iconLabel = new JLabel("✗");
        iconLabel.setFont(new Font("Segoe UI", Font.BOLD, 48));
        iconLabel.setForeground(new Color(220, 53, 69));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Thông báo lỗi
        JLabel messageLabel = new JLabel("<html><div style='text-align: center;'>" +
                "<h2>Đăng ký thất bại!</h2>" +
                "<p>" + message + "</p>" +
                "<p>Vui lòng thử lại với thông tin khác.</p>" +
                "</div></html>");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Nút OK
        JButton okButton = new JButton("Thử lại");
        okButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        okButton.setBackground(new Color(220, 53, 69));
        okButton.setForeground(Color.WHITE);
        okButton.setFocusPainted(false);
        okButton.setPreferredSize(new Dimension(100, 35));
        okButton.addActionListener(e -> dispose());
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(okButton);
        
        mainPanel.add(iconLabel, BorderLayout.NORTH);
        mainPanel.add(messageLabel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private void setupFrame() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(getParent());
    }
}
