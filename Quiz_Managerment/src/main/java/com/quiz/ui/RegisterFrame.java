package com.quiz.ui;

import com.quiz.bus.UserService;
import com.quiz.model.Role;
import com.quiz.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Giao diện đăng ký tài khoản (2 cột: trái chào mừng, phải form tạo tài khoản)
 */
public class RegisterFrame extends JFrame {
    private JTextField usernameField, emailField;
    private JPasswordField passwordField, confirmPasswordField;
    private JComboBox<Role> roleComboBox;
    private JButton registerButton, cancelButton, goLoginButton;
    private UserService userService;

    public RegisterFrame() {
        userService = new UserService();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setupFrame();
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

    private void initializeComponents() {
        // Text fields
        usernameField = new JTextField(20);
        emailField = new JTextField(20);
        passwordField = new JPasswordField(20);
        confirmPasswordField = new JPasswordField(20);
        
        // Role combo box
        roleComboBox = new JComboBox<>();
        loadRoles();
        
        // Buttons
        registerButton = new JButton("Đăng ký");
        cancelButton = new JButton("THOÁT");
        goLoginButton = new JButton("Đăng nhập");
        
        // Thiết lập font
        Font font = new Font("Segoe UI", Font.PLAIN, 14);
        usernameField.setFont(font);
        emailField.setFont(font);
        passwordField.setFont(font);
        confirmPasswordField.setFont(font);
        roleComboBox.setFont(font);
        registerButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cancelButton.setFont(font);
        goLoginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        // Thiết lập màu sắc
        registerButton.setBackground(Color.BLACK);
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        cancelButton.setBackground(Color.BLACK);
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        
        goLoginButton.setBackground(Color.BLACK);
        goLoginButton.setForeground(Color.WHITE);
        goLoginButton.setFocusPainted(false);
        goLoginButton.setBorder(null);
        goLoginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        JPanel container = new JPanel(new GridLayout(1, 2));
        
        // Left gradient welcome panel
        JPanel left = new JPanel() {
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
        left.setLayout(new GridBagLayout());
        GridBagConstraints lbc = new GridBagConstraints();
        lbc.insets = new Insets(10, 10, 10, 10);
        lbc.gridx = 0; lbc.anchor = GridBagConstraints.CENTER;
        JLabel welcome = new JLabel();
        try {
            ImageIcon icon = loadScaledLogo("/images/logo.png", 360, 260);
            if (icon != null) {
                welcome.setIcon(icon);
            } else {
                welcome.setText("QUIZ");
                welcome.setForeground(Color.WHITE);
                welcome.setFont(new Font("Segoe UI", Font.BOLD, 26));
            }
        } catch (Exception ignore) {
            welcome.setText("QUIZ");
            welcome.setForeground(Color.WHITE);
            welcome.setFont(new Font("Segoe UI", Font.BOLD, 26));
        }
        JLabel subtitle = new JLabel("Đăng ký tài khoản để tiếp tục");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(new Color(224, 242, 241));
        // Logo
        lbc.gridy = 0; lbc.insets = new Insets(0, 10, 8, 10); left.add(welcome, lbc);
        // Subtitle closer to logo
        lbc.gridy = 1; lbc.insets = new Insets(0, 10, 6, 10); left.add(subtitle, lbc);
        // Button slightly below subtitle
        lbc.gridy = 2; lbc.insets = new Insets(8, 10, 0, 10); left.add(goLoginButton, lbc);
        
        // Right create account form panel
        JPanel right = new JPanel(new GridBagLayout());
        right.setBackground(Color.WHITE);
        right.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.gridx = 0; gbc.anchor = GridBagConstraints.CENTER; gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JLabel title = new JLabel("Tạo tài khoản");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(0, 0, 0));
        
        usernameField.setToolTipText("Tên đăng nhập");
        emailField.setToolTipText("Email");
        passwordField.setToolTipText("Mật khẩu");
        confirmPasswordField.setToolTipText("Xác nhận mật khẩu");
        
        JLabel nameLabel = new JLabel("Tên đăng nhập");
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        nameLabel.setForeground(new Color(100, 116, 139));
        
        JLabel emailLabel = new JLabel("Email");
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        emailLabel.setForeground(new Color(100, 116, 139));
        
        JLabel passLabel = new JLabel("Mật khẩu");
        passLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        passLabel.setForeground(new Color(100, 116, 139));
        
        JLabel confirmLabel = new JLabel("Xác nhận mật khẩu");
        confirmLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        confirmLabel.setForeground(new Color(100, 116, 139));
        
        JLabel roleLabel = new JLabel("Vai trò");
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        roleLabel.setForeground(new Color(100, 116, 139));
        
        gbc.gridy = 0; right.add(title, gbc);
        gbc.gridy = 1; right.add(nameLabel, gbc);
        gbc.gridy = 2; right.add(withIcon(usernameField, "/images/user.png"), gbc);
        gbc.gridy = 3; right.add(emailLabel, gbc);
        gbc.gridy = 4; right.add(withIcon(emailField, "/images/mail.png"), gbc);
        gbc.gridy = 5; right.add(passLabel, gbc);
        gbc.gridy = 6; right.add(withIcon(passwordField, "/images/padlock.png"), gbc);
        gbc.gridy = 7; right.add(confirmLabel, gbc);
        gbc.gridy = 8; right.add(withIcon(confirmPasswordField, "/images/padlock.png"), gbc);
        gbc.gridy = 9; right.add(roleLabel, gbc);
        gbc.gridy = 10; right.add(roleComboBox, gbc);
        gbc.gridy = 11; right.add(registerButton, gbc);
        
        container.add(left);
        container.add(right);
        add(container, BorderLayout.CENTER);
    }

    private void setupEventHandlers() {
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performRegister();
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        goLoginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openLoginFrame();
            }
        });
        
        // Đăng ký bằng Enter
        KeyStroke enterKeyStroke = KeyStroke.getKeyStroke("ENTER");
        InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getRootPane().getActionMap();
        
        inputMap.put(enterKeyStroke, "register");
        actionMap.put("register", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performRegister();
            }
        });
    }

    private void performRegister() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        Role selectedRole = (Role) roleComboBox.getSelectedItem();
        
        // Validation
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showValidationError("Vui lòng điền đầy đủ thông tin!");
            return;
        }
        
        if (username.length() < 3) {
            showValidationError("Tên đăng nhập phải có ít nhất 3 ký tự!");
            return;
        }
        
        if (password.length() < 6) {
            showValidationError("Mật khẩu phải có ít nhất 6 ký tự!");
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            showValidationError("Mật khẩu xác nhận không khớp!");
            confirmPasswordField.setText("");
            confirmPasswordField.requestFocus();
            return;
        }
        
        if (!isValidEmail(email)) {
            showValidationError("Email không hợp lệ!");
            return;
        }
        
        // Kiểm tra username và email đã tồn tại chưa
        if (userService.isUsernameExists(username)) {
            showValidationError("Tên đăng nhập đã tồn tại! Vui lòng chọn tên khác.");
            usernameField.requestFocus();
            return;
        }
        
        if (userService.isEmailExists(email)) {
            showValidationError("Email đã tồn tại! Vui lòng sử dụng email khác.");
            emailField.requestFocus();
            return;
        }
        
        // Hiển thị loading
        registerButton.setText("Đang đăng ký...");
        registerButton.setEnabled(false);
        
        // Thực hiện đăng ký trong thread riêng
        SwingUtilities.invokeLater(() -> {
            User newUser = new User(username, password, email, selectedRole.getId());
            boolean success = userService.createUser(newUser);
            
            SwingUtilities.invokeLater(() -> {
                registerButton.setText("Đăng ký");
                registerButton.setEnabled(true);
                
                if (success) {
                    // Hiển thị dialog thành công
                    RegisterSuccessDialog successDialog = new RegisterSuccessDialog(this, username);
                    successDialog.setVisible(true);
                    
                    // Mở giao diện đăng nhập
                    openLoginFrame();
                    dispose();
                } else {
                    // Hiển thị dialog lỗi
                    RegisterErrorDialog errorDialog = new RegisterErrorDialog(this, 
                        "Có lỗi xảy ra khi đăng ký. Vui lòng thử lại.");
                    errorDialog.setVisible(true);
                }
            });
        });
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    private void showValidationError(String message) {
        ValidationErrorDialog errorDialog = new ValidationErrorDialog(this, message);
        errorDialog.setVisible(true);
    }

    private void openLoginFrame() {
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
        dispose();
    }

    private void loadRoles() {
        try {
            List<Role> roles = userService.getAllRoles();
            for (Role role : roles) {
                // Chỉ hiển thị student (id = 2), bỏ teacher và admin
                if (role.getId() == 2) { // 2 = student
                    roleComboBox.addItem(role);
                    break; // Chỉ cần tìm thấy student là đủ
                }
            }
            
            // Nếu không có roles nào, thêm fallback chỉ student
            if (roleComboBox.getItemCount() == 0) {
                roleComboBox.addItem(new Role(2, "student"));
            }
        } catch (Exception e) {
            System.err.println("Lỗi load roles: " + e.getMessage());
            // Fallback nếu có lỗi - chỉ student
            roleComboBox.addItem(new Role(2, "student"));
        }
    }

    private void setupFrame() {
        setTitle("Đăng ký tài khoản - Hệ thống quản lý trắc nghiệm");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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
