package com.quiz.ui;

import com.quiz.model.User;
import com.quiz.ui.panels.*;

import javax.swing.*;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Giao diện chính của ứng dụng
 */
public class MainFrame extends JFrame {
    private User currentUser;
    private JTabbedPane tabbedPane;
    private java.util.List<JButton> sidebarButtons = new java.util.ArrayList<>();
    private final Color SIDEBAR_BG = Color.BLACK;
    private final Color SIDEBAR_TEXT = Color.WHITE;
    private final Color ACTIVE_BG = new Color(70, 130, 180);
    private final Color ACTIVE_BORDER = new Color(30, 144, 255);

    public MainFrame(User user) {
        this.currentUser = user;
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setupFrame();
    }

    private void initializeComponents() {
        tabbedPane = new JTabbedPane(JTabbedPane.LEFT);
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = createHeaderBar();
        add(headerPanel, BorderLayout.NORTH);

        setJMenuBar(null);

        // Status bar
        JPanel statusPanel = createStatusBar();
        add(statusPanel, BorderLayout.SOUTH);

        addTabsBasedOnRole();
        completelyHideTabHeaders();
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(tabbedPane, BorderLayout.CENTER);
        centerPanel.add(createSidebar(), BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);

        tabbedPane.addChangeListener(e -> {
            SwingUtilities.invokeLater(() -> updateSidebarSelection());
        });
        
        SwingUtilities.invokeLater(() -> {
            if (tabbedPane.getTabCount() > 0) {
                tabbedPane.setSelectedIndex(0);
                updateSidebarSelection();
            }
        });
    }

    private void completelyHideTabHeaders() {
        tabbedPane.setUI(new BasicTabbedPaneUI() {
            @Override
            protected int calculateTabAreaHeight(int tabPlacement, int runCount, int maxTabHeight) {
                return 0;
            }
            
            @Override
            protected int calculateTabAreaWidth(int tabPlacement, int runCount, int maxTabWidth) {
                return 0;
            }
            
            @Override
            protected void paintTabArea(Graphics g, int tabPlacement, int selectedIndex) {
                // No-op
            }
            
            @Override
            protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
                // No-op
            }
            
            @Override
            protected void paintTab(Graphics g, int tabPlacement, Rectangle[] rects, 
                                   int tabIndex, Rectangle iconRect, Rectangle textRect) {
                // No-op
            }
            
            @Override
            protected void paintFocusIndicator(Graphics g, int tabPlacement, Rectangle[] rects,
                                              int tabIndex, Rectangle iconRect, Rectangle textRect,
                                              boolean isSelected) {
                // No-op
            }
        });
        
        tabbedPane.setBorder(null);
        tabbedPane.setBackground(null);
        tabbedPane.setOpaque(false);
    }

    private JPanel createHeaderBar() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        header.setBackground(Color.BLACK);

        JLabel titleLabel = new JLabel("Quản Lý Trắc Nghiệm");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        header.add(titleLabel, BorderLayout.WEST);
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
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 16, 20, 16));
        sidebar.setBackground(SIDEBAR_BG);

        JLabel sidebarHeader = new JLabel("MENU");
        sidebarHeader.setFont(new Font("Segoe UI", Font.BOLD, 12));
        sidebarHeader.setForeground(new Color(200, 200, 200));
        sidebarHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebarHeader.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        sidebar.add(sidebarHeader);

        sidebarButtons.clear();
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            final int index = i;
            String title = tabbedPane.getTitleAt(i);
            JButton btn = createSidebarButton(title, index);
            sidebar.add(btn);
            sidebar.add(Box.createVerticalStrut(8));
            sidebarButtons.add(btn);
        }

        sidebar.add(Box.createVerticalGlue());

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(SIDEBAR_BG);
        
        int responsiveWidth = calculateResponsiveSidebarWidth();
        wrapper.setPreferredSize(new Dimension(responsiveWidth, 0));
        wrapper.setMinimumSize(new Dimension(240, 0));
        wrapper.setMaximumSize(new Dimension(320, Integer.MAX_VALUE));
        
        wrapper.add(sidebar, BorderLayout.NORTH);
        return wrapper;
    }

    private JButton createSidebarButton(String title, int index) {
        JButton btn = new JButton(title);
        btn.setIcon(resolveTabIcon(title));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setIconTextGap(12);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        int buttonWidth = Math.min(280, calculateResponsiveSidebarWidth() - 32);
        btn.setMaximumSize(new Dimension(buttonWidth, 56));
        btn.setPreferredSize(new Dimension(buttonWidth, 56));
        btn.setMinimumSize(new Dimension(200, 56));
        
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(true);
        btn.setOpaque(true);
        btn.setBackground(SIDEBAR_BG);
        btn.setForeground(SIDEBAR_TEXT);
        btn.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        btn.setBorderPainted(false);
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (tabbedPane.getSelectedIndex() != index) {
                    btn.setBackground(new Color(40, 40, 40));
                }
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (tabbedPane.getSelectedIndex() != index) {
                    btn.setBackground(SIDEBAR_BG);
                }
            }
        });
        
        btn.addActionListener(e -> {
            tabbedPane.setSelectedIndex(index);
            updateSidebarSelection();
        });
        
        return btn;
    }

    private int calculateResponsiveSidebarWidth() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width;
        
        if (screenWidth >= 1920) {
            return 300;
        } else if (screenWidth >= 1366) {
            return 280;
        } else if (screenWidth >= 1024) {
            return 260;
        } else {
            return 240;
        }
    }

    private void updateSidebarSelection() {
        int selected = tabbedPane.getSelectedIndex();
        
        for (int i = 0; i < sidebarButtons.size(); i++) {
            JButton btn = sidebarButtons.get(i);
            
            btn.setBorderPainted(false);
            btn.setBorder(null);
            
            if (i == selected) {
                btn.setBackground(ACTIVE_BG);
                btn.setForeground(Color.WHITE);
                btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
                btn.setBorderPainted(true);
                btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 0, 0, ACTIVE_BORDER),
                    BorderFactory.createEmptyBorder(12, 12, 12, 16)
                ));
            } else {
                btn.setBackground(SIDEBAR_BG);
                btn.setForeground(SIDEBAR_TEXT);
                btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                btn.setBorderPainted(false);
                btn.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
            }
            
            btn.invalidate();
            btn.validate();
            btn.repaint();
        }
    }

    private Icon resolveTabIcon(String title) {
        String t = title.toLowerCase();
        if (t.contains("người dùng") || t.contains("user")) {
            return tintIcon(IconUtil.load("/images/user.png", 16, 16), Color.WHITE);
        }
        if (t.contains("môn học") || t.contains("subject")) {
            return tintIcon(IconUtil.load("/images/subject.png", 16, 16), Color.WHITE);
        }
        if (t.contains("chủ đề") || t.contains("topic")) {
            return tintIcon(IconUtil.load("/images/topic.png", 16, 16), Color.WHITE);
        }
        if (t.contains("câu hỏi") || t.contains("question")) {
            return tintIcon(IconUtil.load("/images/question.png", 16, 16), Color.WHITE);
        }
        if (t.contains("đề thi") || t.contains("exam")) {
            return tintIcon(IconUtil.load("/images/exam.png", 16, 16), Color.WHITE);
        }
        if (t.contains("kết quả")) {
            return tintIcon(IconUtil.load("/images/exam-results.png", 16, 16), Color.WHITE);
        }
        if (t.contains("thống kê") || t.contains("stat")) {
            Icon icon = IconUtil.load("/images/chart.png", 16, 16);
            if (icon == null) {
                icon = IconUtil.load("/images/exam-results.png", 16, 16);
            }
            return tintIcon(icon, Color.WHITE);
        }
        return null;
    }

    private Icon tintIcon(Icon icon, Color tintColor) {
        if (icon == null) return null;
        
        ImageIcon imageIcon = (ImageIcon) icon;
        Image image = imageIcon.getImage();
        
        BufferedImage bufferedImage = new BufferedImage(
            image.getWidth(null), 
            image.getHeight(null), 
            BufferedImage.TYPE_INT_ARGB
        );
        
        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.drawImage(image, 0, 0, null);
        g2d.setComposite(AlphaComposite.SrcAtop);
        g2d.setColor(tintColor);
        g2d.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
        g2d.dispose();
        
        return new ImageIcon(bufferedImage);
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
        tabbedPane.addTab("Quản lý người dùng", IconUtil.load("/images/user.png", 16, 16), new UserManagementPanel());
        tabbedPane.addTab("Quản lý môn học", IconUtil.load("/images/subject.png", 16, 16), new SubjectManagementPanel());
        tabbedPane.addTab("Quản lý chủ đề", IconUtil.load("/images/topic.png", 16, 16), new TopicManagementPanel());
        tabbedPane.addTab("Quản lý câu hỏi", IconUtil.load("/images/question.png", 16, 16), new QuestionManagementPanel(currentUser));
        tabbedPane.addTab("Quản lý đề thi", IconUtil.load("/images/exam.png", 16, 16), new ExamManagementPanel(currentUser));
        tabbedPane.addTab("Kết quả thi", IconUtil.load("/images/exam-results.png", 16, 16), new ExamResultPanel());
        tabbedPane.addTab("Thống kê", IconUtil.load("/images/chart.png", 16, 16), new StatisticsPanel());
    }

    private void addTeacherTabs() {
        tabbedPane.addTab("Quản lý câu hỏi", IconUtil.load("/images/question.png", 16, 16), new QuestionManagementPanel(currentUser));
        tabbedPane.addTab("Quản lý đề thi", IconUtil.load("/images/exam.png", 16, 16), new ExamManagementPanel(currentUser));
        tabbedPane.addTab("Kết quả thi", IconUtil.load("/images/exam-results.png", 16, 16), new ExamResultPanel());
        tabbedPane.addTab("Thống kê", IconUtil.load("/images/chart.png", 16, 16), new StatisticsPanel());
    }

    private void addStudentTabs() {
        tabbedPane.addTab("Đề thi", IconUtil.load("/images/exam.png", 16, 16), new StudentExamPanel(currentUser));
        tabbedPane.addTab("Kết quả của tôi", IconUtil.load("/images/exam-results.png", 16, 16), new MyExamResultPanel(currentUser));
    }

    private void setupEventHandlers() {
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                int option = JOptionPane.showConfirmDialog(
                        MainFrame.this,
                        "Bạn có chắc chắn muốn thoát?",
                        "Xác nhận",
                        JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });

        addWindowStateListener(new java.awt.event.WindowStateListener() {
            @Override
            public void windowStateChanged(java.awt.event.WindowEvent e) {
                int oldState = e.getOldState();
                int newState = e.getNewState();

                if ((oldState & JFrame.MAXIMIZED_BOTH) != 0 && (newState & JFrame.NORMAL) != 0) {
                    SwingUtilities.invokeLater(() -> {
                        setSize(1200, 800);
                        setMinimumSize(new Dimension(1000, 700));
                        setLocationRelativeTo(null);
                        revalidate();
                        repaint();
                    });
                }

                if ((oldState & JFrame.NORMAL) != 0 && (newState & JFrame.MAXIMIZED_BOTH) != 0) {
                    SwingUtilities.invokeLater(() -> {
                        revalidate();
                        repaint();
                    });
                }
            }
        });
    }

    private void setupFrame() {
        setTitle("Hệ thống quản lý trắc nghiệm - " + currentUser.getUsername());
        setMinimumSize(new Dimension(1000, 700));
        setPreferredSize(new Dimension(1200, 800));
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);

        try {
            setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icon.png")));
        } catch (Exception e) {
            // Icon not found
        }
    }

    private void logout() {
        int option = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn đăng xuất?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION);

        if (option == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame().setVisible(true);
        }
    }

}