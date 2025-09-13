package com.quiz.ui;

import com.quiz.model.User;
import com.quiz.ui.panels.*;

import javax.swing.*;
import java.awt.*;

/**
 * Giao diện chính của ứng dụng
 */
public class MainFrame extends JFrame {
    private User currentUser;
    private JTabbedPane tabbedPane;

    public MainFrame(User user) {
        this.currentUser = user;
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setupFrame();
    }

    private void initializeComponents() {
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Menu bar
        JMenuBar menuBar = createMenuBar();
        setJMenuBar(menuBar);
        
        // Status bar
        JPanel statusPanel = createStatusBar();
        add(statusPanel, BorderLayout.SOUTH);
        
        // Tabbed pane
        add(tabbedPane, BorderLayout.CENTER);
        
        // Thêm các tab dựa trên quyền của user
        addTabsBasedOnRole();
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // Menu Hệ thống
        JMenu systemMenu = new JMenu("Hệ thống");
        systemMenu.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JMenuItem logoutItem = new JMenuItem("Đăng xuất");
        logoutItem.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        logoutItem.addActionListener(e -> logout());
        
        JMenuItem exitItem = new JMenuItem("Thoát");
        exitItem.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        exitItem.addActionListener(e -> System.exit(0));
        
        systemMenu.add(logoutItem);
        systemMenu.addSeparator();
        systemMenu.add(exitItem);
        
        // Menu Trợ giúp
        JMenu helpMenu = new JMenu("Trợ giúp");
        helpMenu.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JMenuItem aboutItem = new JMenuItem("Giới thiệu");
        aboutItem.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        aboutItem.addActionListener(e -> showAbout());
        
        helpMenu.add(aboutItem);
        
        menuBar.add(systemMenu);
        menuBar.add(helpMenu);
        
        return menuBar;
    }

    private JPanel createStatusBar() {
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createEtchedBorder());
        statusPanel.setBackground(new Color(240, 240, 240));
        
        JLabel userLabel = new JLabel("Người dùng: " + currentUser.getUsername() + 
                                    " (" + currentUser.getRole().getName() + ")");
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        JLabel timeLabel = new JLabel();
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        updateTimeLabel(timeLabel);
        
        // Cập nhật thời gian mỗi giây
        Timer timer = new Timer(1000, e -> updateTimeLabel(timeLabel));
        timer.start();
        
        statusPanel.add(userLabel, BorderLayout.WEST);
        statusPanel.add(timeLabel, BorderLayout.EAST);
        
        return statusPanel;
    }

    private void updateTimeLabel(JLabel timeLabel) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        timeLabel.setText(sdf.format(new java.util.Date()));
    }

    private void addTabsBasedOnRole() {
        String roleName = currentUser.getRole().getName().toLowerCase();
        
        switch (roleName) {
            case "admin":
                addAdminTabs();
                break;
            case "teacher":
                addTeacherTabs();
                break;
            case "student":
                addStudentTabs();
                break;
        }
    }

    private void addAdminTabs() {
        // Quản lý người dùng
        tabbedPane.addTab("Quản lý người dùng", new UserManagementPanel());
        
        // Quản lý môn học
        tabbedPane.addTab("Quản lý môn học", new SubjectManagementPanel());
        
        // Quản lý chủ đề
        tabbedPane.addTab("Quản lý chủ đề", new TopicManagementPanel());
        
        // Quản lý câu hỏi
        tabbedPane.addTab("Quản lý câu hỏi", new QuestionManagementPanel(currentUser));
        
        // Quản lý đề thi
        tabbedPane.addTab("Quản lý đề thi", new ExamManagementPanel(currentUser));
        
        // Xem kết quả thi
        tabbedPane.addTab("Kết quả thi", new ExamResultPanel());
    }

    private void addTeacherTabs() {
        // Quản lý câu hỏi
        tabbedPane.addTab("Quản lý câu hỏi", new QuestionManagementPanel(currentUser));
        
        // Quản lý đề thi
        tabbedPane.addTab("Quản lý đề thi", new ExamManagementPanel(currentUser));
        
        // Xem kết quả thi
        tabbedPane.addTab("Kết quả thi", new ExamResultPanel());
    }

    private void addStudentTabs() {
        // Danh sách đề thi
        tabbedPane.addTab("Đề thi", new StudentExamPanel(currentUser));
        
        // Kết quả của tôi
        tabbedPane.addTab("Kết quả của tôi", new MyExamResultPanel(currentUser));
    }

    private void setupEventHandlers() {
        // Xử lý đóng cửa sổ
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                int option = JOptionPane.showConfirmDialog(
                    MainFrame.this,
                    "Bạn có chắc chắn muốn thoát?",
                    "Xác nhận",
                    JOptionPane.YES_NO_OPTION
                );
                if (option == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });
    }

    private void setupFrame() {
        setTitle("Hệ thống quản lý trắc nghiệm - " + currentUser.getUsername());
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        
        // Thiết lập icon
        try {
            setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icon.png")));
        } catch (Exception e) {
            // Icon không tồn tại, bỏ qua
        }
    }

    private void logout() {
        int option = JOptionPane.showConfirmDialog(
            this,
            "Bạn có chắc chắn muốn đăng xuất?",
            "Xác nhận",
            JOptionPane.YES_NO_OPTION
        );
        
        if (option == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame().setVisible(true);
        }
    }

    private void showAbout() {
        String message = "Hệ thống quản lý trắc nghiệm\n" +
                        "Phiên bản: 1.0.0\n" +
                        "Phát triển bởi: Java Team\n" +
                        "Công nghệ: Java Swing, SQL Server";
        
        JOptionPane.showMessageDialog(this, message, "Giới thiệu", JOptionPane.INFORMATION_MESSAGE);
    }
}
