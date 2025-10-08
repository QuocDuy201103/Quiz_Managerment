package com.quiz.ui.panels;

import com.quiz.dao.UserDAO;
import com.quiz.model.Role;
import com.quiz.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel quản lý người dùng
 */
public class UserManagementPanel extends JPanel {
    private JTable userTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton addButton, editButton, deleteButton, refreshButton;
    private UserDAO userDAO;
    private List<User> users;

    public UserManagementPanel() {
        userDAO = new UserDAO();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadUsers();
    }

    private void initializeComponents() {
        // Table
        String[] columnNames = {"ID", "Tên đăng nhập", "Email", "Vai trò", "Ngày tạo", "Đăng nhập cuối"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        userTable = new JTable(tableModel);
        userTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        userTable.setRowHeight(25);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Scroll pane
        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setPreferredSize(new Dimension(800, 400));
        
        // Search field
        searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        // Buttons
        addButton = new JButton("Thêm mới", com.quiz.ui.IconUtil.load("/images/add.png", 16, 16));
        editButton = new JButton("Sửa", com.quiz.ui.IconUtil.load("/images/edit.png", 16, 16));
        deleteButton = new JButton("Xóa", com.quiz.ui.IconUtil.load("/images/delete.png", 16, 16));
        refreshButton = new JButton("Làm mới", com.quiz.ui.IconUtil.load("/images/refresh.png", 16, 16));
        
        Font buttonFont = new Font("Segoe UI", Font.PLAIN, 12);
        addButton.setFont(buttonFont);
        editButton.setFont(buttonFont);
        deleteButton.setFont(buttonFont);
        refreshButton.setFont(buttonFont);

    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Top panel - Search and buttons
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Tìm kiếm:"));
        topPanel.add(searchField);
        topPanel.add(Box.createHorizontalStrut(20));
        topPanel.add(addButton);
        topPanel.add(editButton);
        topPanel.add(deleteButton);
        topPanel.add(refreshButton);
        
        // Center panel - Table
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(new JScrollPane(userTable), BorderLayout.CENTER);
        
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }

    private void setupEventHandlers() {
        addButton.addActionListener(e -> showAddUserDialog());
        editButton.addActionListener(e -> showEditUserDialog());
        deleteButton.addActionListener(e -> deleteSelectedUser());
        refreshButton.addActionListener(e -> loadUsers());
        
        // Double click to edit
        userTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    showEditUserDialog();
                }
            }
        });
    }

    private void loadUsers() {
        users = userDAO.getAllUsers();
        updateTable();
    }

    private void updateTable() {
        tableModel.setRowCount(0);
        for (User user : users) {
            Object[] row = {
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().getName(),
                user.getCreatedAt().toString().substring(0, 19),
                user.getLastLogin() != null ? user.getLastLogin().toString().substring(0, 19) : "Chưa đăng nhập"
            };
            tableModel.addRow(row);
        }
    }

    private void showAddUserDialog() {
        UserDialog dialog = new UserDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Thêm người dùng mới", null);
        dialog.setVisible(true);
        if (dialog.isUserAdded()) {
            loadUsers();
        }
    }

    private void showEditUserDialog() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn người dùng cần sửa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        User selectedUser = users.get(selectedRow);
        UserDialog dialog = new UserDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Sửa thông tin người dùng", selectedUser);
        dialog.setVisible(true);
        if (dialog.isUserAdded()) {
            loadUsers();
        }
    }

    private void deleteSelectedUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn người dùng cần xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        User selectedUser = users.get(selectedRow);
        int option = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc chắn muốn xóa người dùng '" + selectedUser.getUsername() + "'?", 
            "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        
        if (option == JOptionPane.YES_OPTION) {
            if (userDAO.deleteUser(selectedUser.getId())) {
                JOptionPane.showMessageDialog(this, "Xóa người dùng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadUsers();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa người dùng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Inner class for User Dialog
    private class UserDialog extends JDialog {
        private JTextField usernameField, emailField;
        private JPasswordField passwordField;
        private JComboBox<Role> roleComboBox;
        private JButton saveButton, cancelButton;
        private boolean userAdded = false;
        private User user;

        public UserDialog(JFrame parent, String title, User user) {
            super(parent, title, true);
            this.user = user;
            initializeComponents();
            setupLayout();
            setupEventHandlers();
            setupDialog();
        }

        private void initializeComponents() {
            usernameField = new JTextField(20);
            passwordField = new JPasswordField(20);
            emailField = new JTextField(20);
            
            // Role combo box
            roleComboBox = new JComboBox<>();
            
            // Load roles from database
            try {
                List<Role> roles = userDAO.getAllRoles();
                for (Role role : roles) {
                    roleComboBox.addItem(role);
                }
            } catch (Exception e) {
                System.err.println("Lỗi load roles in UserManagementPanel: " + e.getMessage());
                // Fallback nếu có lỗi
                roleComboBox.addItem(new Role(0, "admin"));
                roleComboBox.addItem(new Role(1, "teacher"));
                roleComboBox.addItem(new Role(2, "student"));
            }
            
            saveButton = new JButton("Lưu", com.quiz.ui.IconUtil.load("/images/add.png", 16, 16));
            cancelButton = new JButton("Hủy", com.quiz.ui.IconUtil.load("/images/delete.png", 16, 16));
            
            Font font = new Font("Segoe UI", Font.PLAIN, 12);
            usernameField.setFont(font);
            passwordField.setFont(font);
            emailField.setFont(font);
            roleComboBox.setFont(font);
            saveButton.setFont(font);
            cancelButton.setFont(font);
            
            
            // Nếu là edit mode, điền thông tin
            if (user != null) {
                usernameField.setText(user.getUsername());
                emailField.setText(user.getEmail());
                passwordField.setText(user.getPassword());
                
                // Set role
                for (int i = 0; i < roleComboBox.getItemCount(); i++) {
                    Role role = (Role) roleComboBox.getItemAt(i);
                    if (role.getId() == user.getRoleId()) {
                        roleComboBox.setSelectedIndex(i);
                        break;
                    }
                }
            }
        }

        private void setupLayout() {
            setLayout(new BorderLayout());
            
            JPanel mainPanel = new JPanel(new GridBagLayout());
            mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.anchor = GridBagConstraints.WEST;
            
            // Username
            gbc.gridx = 0; gbc.gridy = 0;
            mainPanel.add(new JLabel("Tên đăng nhập:"), gbc);
            gbc.gridx = 1;
            mainPanel.add(usernameField, gbc);
            
            // Password
            gbc.gridx = 0; gbc.gridy = 1;
            mainPanel.add(new JLabel("Mật khẩu:"), gbc);
            gbc.gridx = 1;
            mainPanel.add(passwordField, gbc);
            
            // Email
            gbc.gridx = 0; gbc.gridy = 2;
            mainPanel.add(new JLabel("Email:"), gbc);
            gbc.gridx = 1;
            mainPanel.add(emailField, gbc);
            
            // Role
            gbc.gridx = 0; gbc.gridy = 3;
            mainPanel.add(new JLabel("Vai trò:"), gbc);
            gbc.gridx = 1;
            mainPanel.add(roleComboBox, gbc);
            
            // Buttons
            JPanel buttonPanel = new JPanel(new FlowLayout());
            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);
            
            add(mainPanel, BorderLayout.CENTER);
            add(buttonPanel, BorderLayout.SOUTH);
        }

        private void setupEventHandlers() {
            saveButton.addActionListener(e -> saveUser());
            cancelButton.addActionListener(e -> dispose());
        }

        private void setupDialog() {
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            setResizable(false);
            pack();
            setLocationRelativeTo(getParent());
        }

        private void saveUser() {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String email = emailField.getText().trim();
            Role selectedRole = (Role) roleComboBox.getSelectedItem();
            
            if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            boolean success;
            if (user == null) {
                // Add new user
                User newUser = new User(username, password, email, selectedRole.getId());
                success = userDAO.addUser(newUser);
            } else {
                // Update existing user
                user.setUsername(username);
                user.setPassword(password);
                user.setEmail(email);
                user.setRoleId(selectedRole.getId());
                success = userDAO.updateUser(user);
            }
            
            if (success) {
                JOptionPane.showMessageDialog(this, "Lưu thông tin thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                userAdded = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi lưu thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }

        public boolean isUserAdded() {
            return userAdded;
        }
    }
}
