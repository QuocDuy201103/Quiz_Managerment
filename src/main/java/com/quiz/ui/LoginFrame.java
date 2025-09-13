package com.quiz.ui;

import com.quiz.dao.UserDAO;
import com.quiz.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Giao diện đăng nhập
 */
public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton, registerButton;
    private UserDAO userDAO;

    public LoginFrame() {
        userDAO = new UserDAO();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setupFrame();
    }

    private void initializeComponents() {
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        loginButton = new JButton("Đăng nhập");
        registerButton = new JButton("Chưa có tài khoản? Đăng ký");
        
        // Thiết lập font
        Font font = new Font("Segoe UI", Font.PLAIN, 14);
        usernameField.setFont(font);
        passwordField.setFont(font);
        loginButton.setFont(font);
        registerButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        // Thiết lập màu sắc
        loginButton.setBackground(new Color(0, 123, 255));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        
        registerButton.setBackground(Color.WHITE);
        registerButton.setForeground(new Color(0, 123, 255));
        registerButton.setBorderPainted(false);
        registerButton.setFocusPainted(false);
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Panel chính
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        mainPanel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Tiêu đề
        JLabel titleLabel = new JLabel("HỆ THỐNG QUẢN LÝ TRẮC NGHIỆM");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 123, 255));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(titleLabel, gbc);
        
        // Khoảng trống
        gbc.gridy = 1;
        mainPanel.add(Box.createVerticalStrut(30), gbc);
        
        // Tên đăng nhập
        JLabel usernameLabel = new JLabel("Tên đăng nhập:");
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(usernameLabel, gbc);
        
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(usernameField, gbc);
        
        // Mật khẩu
        JLabel passwordLabel = new JLabel("Mật khẩu:");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(passwordLabel, gbc);
        
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(passwordField, gbc);
        
        // Nút đăng nhập
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        loginButton.setPreferredSize(new Dimension(200, 40));
        mainPanel.add(loginButton, gbc);
        
        // Nút đăng ký
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(registerButton, gbc);
        
        add(mainPanel, BorderLayout.CENTER);
    }

    private void setupEventHandlers() {
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });
        
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openRegisterFrame();
            }
        });
        
        // Đăng nhập bằng Enter
        KeyStroke enterKeyStroke = KeyStroke.getKeyStroke("ENTER");
        InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getRootPane().getActionMap();
        
        inputMap.put(enterKeyStroke, "login");
        actionMap.put("login", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });
    }

    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", 
                                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Hiển thị loading
        loginButton.setText("Đang đăng nhập...");
        loginButton.setEnabled(false);
        
        // Thực hiện đăng nhập trong thread riêng
        SwingUtilities.invokeLater(() -> {
            User user = userDAO.login(username, password);
            
            SwingUtilities.invokeLater(() -> {
                loginButton.setText("Đăng nhập");
                loginButton.setEnabled(true);
                
                if (user != null) {
                    JOptionPane.showMessageDialog(this, 
                        "Đăng nhập thành công!\nXin chào " + user.getUsername(), 
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    
                    // Mở giao diện chính
                    openMainFrame(user);
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Tên đăng nhập hoặc mật khẩu không đúng!", 
                        "Lỗi đăng nhập", JOptionPane.ERROR_MESSAGE);
                    passwordField.setText("");
                    usernameField.requestFocus();
                }
            });
        });
    }

    private void openMainFrame(User user) {
        SwingUtilities.invokeLater(() -> {
            new MainFrame(user).setVisible(true);
        });
        dispose();
    }

    private void openRegisterFrame() {
        SwingUtilities.invokeLater(() -> {
            new RegisterFrame().setVisible(true);
        });
        dispose();
    }

    private void setupFrame() {
        setTitle("Đăng nhập - Hệ thống quản lý trắc nghiệm");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
        
        // Thiết lập icon
        try {
            setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icon.png")));
        } catch (Exception e) {
            // Icon không tồn tại, bỏ qua
        }
    }
}
