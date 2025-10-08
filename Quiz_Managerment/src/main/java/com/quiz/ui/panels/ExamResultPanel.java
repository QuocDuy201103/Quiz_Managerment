package com.quiz.ui.panels;

import com.quiz.dao.ExamResultDAO;
import com.quiz.model.ExamResult;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel xem kết quả thi (cho admin và teacher)
 */
public class ExamResultPanel extends JPanel {
    private JTable resultTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton refreshButton, viewDetailsButton;
    private ExamResultDAO examResultDAO;
    private List<ExamResult> examResults;

    public ExamResultPanel() {
        examResultDAO = new ExamResultDAO();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadExamResults();
    }

    private void initializeComponents() {
        // Table
        String[] columnNames = {"ID", "Học sinh", "Đề thi", "Điểm", "Thời gian bắt đầu", "Thời gian kết thúc", "Ngày nộp"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        resultTable = new JTable(tableModel);
        resultTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        resultTable.setRowHeight(25);
        resultTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Search field
        searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        // Buttons
        refreshButton = new JButton("Làm mới", com.quiz.ui.IconUtil.load("/images/refresh.png", 16, 16));
        viewDetailsButton = new JButton("Xem chi tiết", com.quiz.ui.IconUtil.load("/images/exam-results.png", 16, 16));
        
        Font buttonFont = new Font("Segoe UI", Font.PLAIN, 12);
        refreshButton.setFont(buttonFont);
        viewDetailsButton.setFont(buttonFont);

    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Top panel - Search and buttons
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Tìm kiếm:"));
        topPanel.add(searchField);
        topPanel.add(Box.createHorizontalStrut(20));
        topPanel.add(refreshButton);
        topPanel.add(viewDetailsButton);
        
        // Center panel - Table
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(new JScrollPane(resultTable), BorderLayout.CENTER);
        
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }

    private void setupEventHandlers() {
        refreshButton.addActionListener(e -> loadExamResults());
        viewDetailsButton.addActionListener(e -> viewResultDetails());
        
        // Double click to view details
        resultTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    viewResultDetails();
                }
            }
        });
    }

    private void loadExamResults() {
        examResults = examResultDAO.getAllExamResults();
        updateTable();
    }

    private void updateTable() {
        tableModel.setRowCount(0);
        for (ExamResult result : examResults) {
            Object[] row = {
                result.getId(),
                result.getUser() != null ? result.getUser().getUsername() : "N/A",
                result.getExam() != null ? result.getExam().getTitle() : "N/A",
                String.format("%.1f/10", result.getScore()),
                result.getStartTime().toString().substring(0, 19),
                result.getEndTime().toString().substring(0, 19),
                result.getSubmittedAt().toString().substring(0, 19)
            };
            tableModel.addRow(row);
        }
    }

    private void viewResultDetails() {
        int selectedRow = resultTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn kết quả cần xem!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        ExamResult selectedResult = examResults.get(selectedRow);
        
        // Tạo dialog hiển thị chi tiết kết quả
        JDialog detailsDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Chi tiết kết quả thi", true);
        detailsDialog.setLayout(new BorderLayout());
        
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Thông tin kết quả
        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(new JLabel("Học sinh:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(new JLabel(selectedResult.getUser() != null ? selectedResult.getUser().getUsername() : "N/A"), gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        mainPanel.add(new JLabel("Đề thi:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(new JLabel(selectedResult.getExam() != null ? selectedResult.getExam().getTitle() : "N/A"), gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        mainPanel.add(new JLabel("Điểm số:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(new JLabel(String.format("%.1f/10", selectedResult.getScore())), gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        mainPanel.add(new JLabel("Thời gian bắt đầu:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(new JLabel(selectedResult.getStartTime().toString()), gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        mainPanel.add(new JLabel("Thời gian kết thúc:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(new JLabel(selectedResult.getEndTime().toString()), gbc);
        
        gbc.gridx = 0; gbc.gridy = 5;
        mainPanel.add(new JLabel("Ngày nộp:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(new JLabel(selectedResult.getSubmittedAt().toString()), gbc);
        
        // Thời gian làm bài
        long durationMinutes = java.time.Duration.between(selectedResult.getStartTime(), selectedResult.getEndTime()).toMinutes();
        gbc.gridx = 0; gbc.gridy = 6;
        mainPanel.add(new JLabel("Thời gian làm bài:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(new JLabel(durationMinutes + " phút"), gbc);
        
        JButton closeButton = new JButton("Đóng");
        closeButton.addActionListener(e -> detailsDialog.dispose());
        closeButton.setBackground(new Color(108, 117, 125));
        closeButton.setForeground(Color.WHITE);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(closeButton);
        
        detailsDialog.add(mainPanel, BorderLayout.CENTER);
        detailsDialog.add(buttonPanel, BorderLayout.SOUTH);
        
        detailsDialog.setResizable(false);
        detailsDialog.pack();
        detailsDialog.setLocationRelativeTo(getParent());
        detailsDialog.setVisible(true);
    }
}
