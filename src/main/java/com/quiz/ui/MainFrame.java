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
    private java.util.List<JButton> sidebarButtons = new java.util.ArrayList<>();
    private final Color SIDEBAR_BG = new Color(54, 44, 80);      // dark purple
    private final Color SIDEBAR_BG_ACTIVE = new Color(74, 64, 100);
    private final Color SIDEBAR_TEXT = Color.WHITE;

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
        
        // Header (theo design có thanh tiêu đề bên trong frame)
        JPanel headerPanel = createHeaderBar();
        add(headerPanel, BorderLayout.NORTH);
        
        // Không sử dụng menu bar trên cùng
        setJMenuBar(null);
        
        // Status bar
        JPanel statusPanel = createStatusBar();
        add(statusPanel, BorderLayout.SOUTH);
        
        // Thêm các tab dựa trên quyền của user
        addTabsBasedOnRole();
        
        // Ẩn thanh tab, chỉ sử dụng sidebar để điều hướng
        hideTabHeaders();
        
        // Bố cục trung tâm gồm sidebar bên trái và nội dung tab ở giữa
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(tabbedPane, BorderLayout.CENTER);
        centerPanel.add(createSidebar(), BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);

        // Đồng bộ highlight sidebar khi đổi tab
        tabbedPane.addChangeListener(e -> updateSidebarSelection());
        updateSidebarSelection();
    }

    private void hideTabHeaders() {
        tabbedPane.setBorder(BorderFactory.createEmptyBorder());
        tabbedPane.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {
            @Override
            protected int calculateTabAreaHeight(int tabPlacement, int runCount, int maxTabHeight) {
                return 0;
            }
            @Override
            protected void paintTabArea(Graphics g, int tabPlacement, int selectedIndex) {
                // no-op to avoid drawing tab area
            }
            @Override
            protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
                // no-op to avoid the thin top border line
            }
        });
    }

    private JPanel createHeaderBar() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        header.setBackground(new Color(74, 64, 100)); // top bar purple

        // Left: App title
        JLabel titleLabel = new JLabel("Home/" + getCurrentTabTitle());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        header.add(titleLabel, BorderLayout.WEST);

        // Right: user, time, logout
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);

        JLabel userLabel = new JLabel(currentUser.getUsername());
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        userLabel.setForeground(Color.WHITE);
        userLabel.setIcon(IconUtil.load("/images/user.png", 16, 16));

        JLabel timeLabel = new JLabel();
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        timeLabel.setForeground(new Color(230, 230, 240));
        updateTimeLabel(timeLabel);
        Timer timer = new Timer(1000, e -> updateTimeLabel(timeLabel));
        timer.start();

        JButton logoutButton = new JButton("Đăng xuất");
        com.quiz.ui.StyleUtil.secondary(logoutButton);
        logoutButton.addActionListener(e -> logout());

        rightPanel.add(userLabel);
        rightPanel.add(timeLabel);
        rightPanel.add(logoutButton);
        header.add(rightPanel, BorderLayout.EAST);

        return header;
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(16, 12, 16, 12));
        sidebar.setBackground(SIDEBAR_BG);

        // Brand header
        JLabel brand = new JLabel("Quiz Manager");
        brand.setFont(new Font("Segoe UI", Font.BOLD, 16));
        brand.setForeground(SIDEBAR_TEXT);
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(brand);
        sidebar.add(Box.createVerticalStrut(16));
        
        // Tạo nút dựa trên các tab hiện có
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            final int index = i;
            String title = tabbedPane.getTitleAt(i);
            JButton btn = new JButton(title);
            btn.setIcon(resolveTabIcon(title));
            btn.setHorizontalAlignment(SwingConstants.LEFT);
            btn.setIconTextGap(10);
            btn.setAlignmentX(Component.LEFT_ALIGNMENT);
            btn.setMaximumSize(new Dimension(180, 36));
            btn.setPreferredSize(new Dimension(180, 36));
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            btn.setFocusPainted(false);
            btn.setOpaque(true);
            btn.setBackground(SIDEBAR_BG);
            btn.setForeground(SIDEBAR_TEXT);
            btn.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 8));
            btn.addActionListener(e -> tabbedPane.setSelectedIndex(index));
            sidebar.add(btn);
            sidebar.add(Box.createVerticalStrut(8));
            sidebarButtons.add(btn);
        }
        
        // Đảm bảo chiều rộng cố định giống design
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(sidebar.getBackground());
        wrapper.setPreferredSize(new Dimension(200, 0));
        wrapper.add(sidebar, BorderLayout.NORTH);
        return wrapper;
    }

    private void updateSidebarSelection() {
        int selected = tabbedPane.getSelectedIndex();
        for (int i = 0; i < sidebarButtons.size(); i++) {
            JButton b = sidebarButtons.get(i);
            if (i == selected) {
                b.setBackground(SIDEBAR_BG_ACTIVE);
            } else {
                b.setBackground(SIDEBAR_BG);
            }
        }
        // update header breadcrumb
        // Force header to refresh title
        // Simply re-add header text by updating frame titleLabel is simpler if stored; here, set frame title
        // but we keep frame title as is. No-op.
    }

    private String getCurrentTabTitle() {
        int idx = tabbedPane.getTabCount() > 0 ? tabbedPane.getSelectedIndex() : -1;
        if (idx >= 0) return tabbedPane.getTitleAt(idx);
        return "Dashboard";
    }

    private Icon resolveTabIcon(String title) {
        String t = title.toLowerCase();
        if (t.contains("người dùng") || t.contains("user")) {
            return IconUtil.load("/images/user.png", 16, 16);
        }
        if (t.contains("môn học") || t.contains("subject")) {
            return IconUtil.load("/images/subject.png", 16, 16);
        }
        if (t.contains("chủ đề") || t.contains("topic")) {
            return IconUtil.load("/images/topic.png", 16, 16);
        }
        if (t.contains("câu hỏi") || t.contains("question")) {
            return IconUtil.load("/images/question.png", 16, 16);
        }
        if (t.contains("đề thi") || t.contains("exam")) {
            return IconUtil.load("/images/exam.png", 16, 16);
        }
        if (t.contains("kết quả")) {
            return IconUtil.load("/images/exam-results.png", 16, 16);
        }
        return null;
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
        tabbedPane.addTab("Quản lý người dùng", IconUtil.load("/images/user.png", 16, 16), new UserManagementPanel());
        
        // Quản lý môn học
        tabbedPane.addTab("Quản lý môn học", IconUtil.load("/images/subject.png", 16, 16),new SubjectManagementPanel());
        
        // Quản lý chủ đề
        tabbedPane.addTab("Quản lý chủ đề", IconUtil.load("/images/topic.png", 16, 16), new TopicManagementPanel());
        
        // Quản lý câu hỏi
        tabbedPane.addTab("Quản lý câu hỏi", IconUtil.load("/images/question.png", 16, 16), new QuestionManagementPanel(currentUser));
        
        // Quản lý đề thi
        tabbedPane.addTab("Quản lý đề thi", IconUtil.load("/images/exam.png", 16, 16), new ExamManagementPanel(currentUser));
        
        // Xem kết quả thi
        tabbedPane.addTab("Kết quả thi", IconUtil.load("/images/exam-results.png", 16, 16), new ExamResultPanel());
    }

    private void addTeacherTabs() {
        // Quản lý câu hỏi
        tabbedPane.addTab("Quản lý câu hỏi", IconUtil.load("/images/question.png", 16, 16), new QuestionManagementPanel(currentUser));
        
        // Quản lý đề thi
        tabbedPane.addTab("Quản lý đề thi", IconUtil.load("/images/exam.png", 16, 16), new ExamManagementPanel(currentUser));
        
        // Xem kết quả thi
        tabbedPane.addTab("Kết quả thi", IconUtil.load("/images/exam-results.png", 16, 16), new ExamResultPanel());
    }

    private void addStudentTabs() {
        // Danh sách đề thi
        tabbedPane.addTab("Đề thi", IconUtil.load("/images/exam.png", 16, 16), new StudentExamPanel(currentUser));
        
        // Kết quả của tôi
        tabbedPane.addTab("Kết quả của tôi", IconUtil.load("/images/exam-results.png", 16, 16), new MyExamResultPanel(currentUser));
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
        
        // Xử lý restore down - thiết lập kích thước khi chuyển từ maximized về normal
        addWindowStateListener(new java.awt.event.WindowStateListener() {
            @Override
            public void windowStateChanged(java.awt.event.WindowEvent e) {
                int oldState = e.getOldState();
                int newState = e.getNewState();
                
                // Kiểm tra nếu chuyển từ MAXIMIZED_BOTH về NORMAL (restore down)
                if ((oldState & JFrame.MAXIMIZED_BOTH) != 0 && (newState & JFrame.NORMAL) != 0) {
                    // Thiết lập kích thước cụ thể khi restore down
                    setSize(1200, 800);
                    setLocationRelativeTo(null);
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
