package com.quiz.ui;

import com.quiz.bus.UserService;
import com.quiz.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Giao diện đăng nhập (2 cột: trái xanh lá giới thiệu, phải form đăng nhập)
 */
public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton, goRegisterButton;
    private UserService userService;

    public LoginFrame() {
        userService = new UserService();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setupFrame();
    }

    private void initializeComponents() {
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        loginButton = new JButton("Đăng nhập");
        goRegisterButton = new JButton("Đăng ký");
        
        Font font = new Font("Segoe UI", Font.PLAIN, 14);
        usernameField.setFont(font);
        passwordField.setFont(font);
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        goRegisterButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        loginButton.setBackground(Color.BLACK);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        goRegisterButton.setBackground(Color.WHITE);
        goRegisterButton.setForeground(Color.BLACK);
        goRegisterButton.setFocusPainted(false);
        goRegisterButton.setBorder(null);
        goRegisterButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private JPanel buildLeftIntroPanel() {
        JPanel leftPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c1 = new Color(0, 0, 0);
                Color c2 = new Color(30, 30, 30);
                GradientPaint gp = new GradientPaint(0, 0, c1, 0, getHeight(), c2);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        leftPanel.setLayout(new GridBagLayout());
        GridBagConstraints lbc = new GridBagConstraints();
        lbc.insets = new Insets(10, 10, 10, 10);
        lbc.gridx = 0; lbc.anchor = GridBagConstraints.CENTER;

        JLabel logoLabel = new JLabel();
        try {
            ImageIcon icon = loadScaledLogo("/images/logo.png", 360, 260);
            if (icon != null) {
                logoLabel.setIcon(icon);
            } else {
                logoLabel.setText("QUIZ");
                logoLabel.setForeground(Color.WHITE);
                logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
            }
        } catch (Exception ignore) {
            logoLabel.setText("QUIZ");
            logoLabel.setForeground(Color.WHITE);
            logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        }

        JLabel subtitle = new JLabel("Đăng nhập để tiếp tục");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(new Color(224, 242, 241));

        JButton goLogin = new JButton("Đăng nhập");
        goLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        goLogin.setBackground(Color.BLACK);
        goLogin.setForeground(Color.WHITE);
        goLogin.setFocusPainted(false);
        goLogin.setBorder(null);
        goLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        goLogin.addActionListener(e -> performLogin());

        // Logo
        lbc.gridy = 0; lbc.insets = new Insets(0, 10, 8, 10); leftPanel.add(logoLabel, lbc);
        // Subtitle placed closer to logo
        lbc.gridy = 1; lbc.insets = new Insets(0, 10, 6, 10); leftPanel.add(subtitle, lbc);
        // Button slightly below subtitle
        lbc.gridy = 2; lbc.insets = new Insets(8, 10, 0, 10); leftPanel.add(goLogin, lbc);
        return leftPanel;
    }

    private JPanel buildRightLoginPanel() {
        JPanel right = new JPanel(new GridBagLayout());
        right.setBackground(Color.WHITE);
        right.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.gridx = 0; gbc.anchor = GridBagConstraints.CENTER; gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Đăng nhập");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(0, 0, 0));

        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        userLabel.setForeground(new Color(100, 116, 139));
        JPanel userField = withIcon(usernameField, "/images/user.png");
        
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        passLabel.setForeground(new Color(100, 116, 139));
        JPanel passField = withIcon(passwordField, "/images/padlock.png");

        gbc.gridy = 0; right.add(title, gbc);
        gbc.gridy = 1; right.add(userLabel, gbc);
        gbc.gridy = 2; right.add(userField, gbc);
        gbc.gridy = 3; right.add(passLabel, gbc);
        gbc.gridy = 4; right.add(passField, gbc);
        gbc.gridy = 5; right.add(loginButton, gbc);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        bottom.setOpaque(false);
        bottom.add(new JLabel("Không có tài khoản?"));
        bottom.add(goRegisterButton);
        gbc.gridy = 6; right.add(bottom, gbc);
        return right;
    }

    private JPanel withIcon(JComponent field, String resourcePath) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        JLabel iconLabel = new JLabel();
        iconLabel.setOpaque(true);
        iconLabel.setBackground(new Color(248, 250, 252));
        iconLabel.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 0, new Color(203, 213, 225)));
        try {
            java.net.URL url = getClass().getResource(resourcePath);
            if (url != null) {
                Image img = new ImageIcon(url).getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH);
                iconLabel.setIcon(new ImageIcon(img));
                iconLabel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(1, 1, 1, 0, new Color(203, 213, 225)),
                    BorderFactory.createEmptyBorder(6, 8, 6, 8)
                ));
            } else {
                iconLabel.setText("  ");
            }
        } catch (Exception ignore) {
            iconLabel.setText("  ");
        }
        field.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 1, new Color(203, 213, 225)));
        panel.add(iconLabel, BorderLayout.WEST);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private ImageIcon loadScaledLogo(String resourcePath, int maxWidth, int maxHeight) {
        java.net.URL url = getClass().getResource(resourcePath);
        if (url == null) return null;
        ImageIcon raw = new ImageIcon(url);
        int w = raw.getIconWidth();
        int h = raw.getIconHeight();
        if (w <= 0 || h <= 0) return null;
        double scale = Math.min((double) maxWidth / w, (double) maxHeight / h);
        int nw = (int) Math.round(w * scale);
        int nh = (int) Math.round(h * scale);
        Image scaled = raw.getImage().getScaledInstance(nw, nh, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        JPanel container = new JPanel(new GridLayout(1, 2));
        container.add(buildLeftIntroPanel());
        container.add(buildRightLoginPanel());
        add(container, BorderLayout.CENTER);
    }

    private void setupEventHandlers() {
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });
        
        goRegisterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openRegisterFrame();
            }
        });
        
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
        
        loginButton.setText("Signing in...");
        loginButton.setEnabled(false);
        
        // Thực hiện đăng nhập trong thread riêng
        SwingUtilities.invokeLater(() -> {
            User user = userService.login(username, password);
            
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
