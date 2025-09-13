package com.quiz.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Dialog thông báo đăng ký thành công
 */
public class RegisterSuccessDialog extends JDialog {
    
    public RegisterSuccessDialog(JFrame parent, String username) {
        super(parent, "Đăng ký thành công", true);
        initializeComponents(username);
        setupLayout();
        setupFrame();
    }
    
    private void initializeComponents(String username) {
        // Components sẽ được tạo trong setupLayout
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        mainPanel.setBackground(Color.WHITE);
        
        // Icon thành công
        JLabel iconLabel = new JLabel("✓");
        iconLabel.setFont(new Font("Segoe UI", Font.BOLD, 48));
        iconLabel.setForeground(new Color(40, 167, 69));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Thông báo
        JLabel messageLabel = new JLabel("<html><div style='text-align: center;'>" +
                "<h2>Đăng ký thành công!</h2>" +
                "<p>Chào mừng bạn đến với hệ thống quản lý trắc nghiệm.</p>" +
                "<p>Bạn có thể đăng nhập ngay bây giờ.</p>" +
                "</div></html>");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Nút OK
        JButton okButton = new JButton("Đăng nhập ngay");
        okButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        okButton.setBackground(new Color(0, 123, 255));
        okButton.setForeground(Color.WHITE);
        okButton.setFocusPainted(false);
        okButton.setPreferredSize(new Dimension(150, 35));
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
