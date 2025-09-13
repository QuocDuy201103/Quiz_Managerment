package com.quiz.ui;

import com.quiz.dao.UserDAO;
import com.quiz.model.Role;
import com.quiz.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Giao diện đăng ký tài khoản
 */
public class RegisterFrame extends JFrame {
    private JTextField usernameField, emailField;
    private JPasswordField passwordField, confirmPasswordField;
    private JComboBox<Role> roleComboBox;
    private JButton registerButton, cancelButton, loginButton;
    private UserDAO userDAO;

    public RegisterFrame() {
        userDAO = new UserDAO();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setupFrame();
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
        cancelButton = new JButton("Hủy");
        loginButton = new JButton("Đã có tài khoản? Đăng nhập");
        
        // Thiết lập font
        Font font = new Font("Segoe UI", Font.PLAIN, 14);
        usernameField.setFont(font);
        emailField.setFont(font);
        passwordField.setFont(font);
        confirmPasswordField.setFont(font);
        roleComboBox.setFont(font);
        registerButton.setFont(font);
        cancelButton.setFont(font);
        loginButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        // Thiết lập màu sắc
        registerButton.setBackground(new Color(40, 167, 69));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        
        cancelButton.setBackground(new Color(108, 117, 125));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        
        loginButton.setBackground(Color.WHITE);
        loginButton.setForeground(new Color(0, 123, 255));
        loginButton.setBorderPainted(false);
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
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
        JLabel titleLabel = new JLabel("ĐĂNG KÝ TÀI KHOẢN");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 123, 255));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(titleLabel, gbc);
        
        // Khoảng trống
        gbc.gridy = 1;
        mainPanel.add(Box.createVerticalStrut(20), gbc);
        
        // Tên đăng nhập
        JLabel usernameLabel = new JLabel("Tên đăng nhập:");
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(usernameLabel, gbc);
        
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(usernameField, gbc);
        
        // Email
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(emailLabel, gbc);
        
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(emailField, gbc);
        
        // Mật khẩu
        JLabel passwordLabel = new JLabel("Mật khẩu:");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0; gbc.gridy = 4; gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(passwordLabel, gbc);
        
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(passwordField, gbc);
        
        // Xác nhận mật khẩu
        JLabel confirmPasswordLabel = new JLabel("Xác nhận mật khẩu:");
        confirmPasswordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0; gbc.gridy = 5; gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(confirmPasswordLabel, gbc);
        
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(confirmPasswordField, gbc);
        
        // Vai trò
        JLabel roleLabel = new JLabel("Vai trò:");
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0; gbc.gridy = 6; gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(roleLabel, gbc);
        
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(roleComboBox, gbc);
        
        // Nút đăng ký và hủy
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(registerButton);
        buttonPanel.add(cancelButton);
        
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        registerButton.setPreferredSize(new Dimension(200, 40));
        mainPanel.add(buttonPanel, gbc);
        
        // Nút đăng nhập
        gbc.gridy = 8;
        gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(loginButton, gbc);
        
        add(mainPanel, BorderLayout.CENTER);
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
        
        loginButton.addActionListener(new ActionListener() {
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
        if (userDAO.isUsernameExists(username)) {
            showValidationError("Tên đăng nhập đã tồn tại! Vui lòng chọn tên khác.");
            usernameField.requestFocus();
            return;
        }
        
        if (userDAO.isEmailExists(email)) {
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
            boolean success = userDAO.addUser(newUser);
            
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
            List<Role> roles = userDAO.getAllRoles();
            for (Role role : roles) {
                // Chỉ hiển thị teacher và student, không hiển thị admin
                if (role.getId() != 0) { // 0 = admin
                    roleComboBox.addItem(role);
                }
            }
            
            // Nếu không có roles nào, thêm fallback
            if (roleComboBox.getItemCount() == 0) {
                roleComboBox.addItem(new Role(1, "teacher"));
                roleComboBox.addItem(new Role(2, "student"));
            }
        } catch (Exception e) {
            System.err.println("Lỗi load roles: " + e.getMessage());
            // Fallback nếu có lỗi
            roleComboBox.addItem(new Role(1, "teacher"));
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
