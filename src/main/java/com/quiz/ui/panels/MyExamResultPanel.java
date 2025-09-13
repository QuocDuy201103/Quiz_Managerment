package com.quiz.ui.panels;

import com.quiz.dao.ExamResultDAO;
import com.quiz.model.ExamResult;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel xem kết quả thi của học sinh
 */
public class MyExamResultPanel extends JPanel {
    private JTable resultTable;
    private DefaultTableModel tableModel;
    private JButton refreshButton, viewDetailsButton;
    private ExamResultDAO examResultDAO;
    private List<ExamResult> myExamResults;
    private int currentUserId = 1; // TODO: Get from current user session

    public MyExamResultPanel() {
        examResultDAO = new ExamResultDAO();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadMyExamResults();
    }

    private void initializeComponents() {
        // Table
        String[] columnNames = {"ID", "Đề thi", "Điểm", "Thời gian bắt đầu", "Thời gian kết thúc", "Ngày nộp"};
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
        
        // Buttons
        refreshButton = new JButton("Làm mới");
        viewDetailsButton = new JButton("Xem chi tiết");
        
        Font buttonFont = new Font("Segoe UI", Font.PLAIN, 12);
        refreshButton.setFont(buttonFont);
        viewDetailsButton.setFont(buttonFont);
        
        // Thiết lập màu sắc cho buttons
        refreshButton.setBackground(new Color(108, 117, 125));
        refreshButton.setForeground(Color.WHITE);
        viewDetailsButton.setBackground(new Color(0, 123, 255));
        viewDetailsButton.setForeground(Color.WHITE);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Top panel - Title and buttons
        JPanel topPanel = new JPanel(new BorderLayout());
        
        JLabel titleLabel = new JLabel("Kết quả thi của tôi");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(0, 123, 255));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(refreshButton);
        buttonPanel.add(viewDetailsButton);
        
        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(buttonPanel, BorderLayout.EAST);
        
        // Center panel - Table
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(new JScrollPane(resultTable), BorderLayout.CENTER);
        
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }

    private void setupEventHandlers() {
        refreshButton.addActionListener(e -> loadMyExamResults());
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

    private void loadMyExamResults() {
        myExamResults = examResultDAO.getExamResultsByUser(currentUserId);
        updateTable();
    }

    private void updateTable() {
        tableModel.setRowCount(0);
        for (ExamResult result : myExamResults) {
            Object[] row = {
                result.getId(),
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
        
        ExamResult selectedResult = myExamResults.get(selectedRow);
        
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
        mainPanel.add(new JLabel("Đề thi:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(new JLabel(selectedResult.getExam() != null ? selectedResult.getExam().getTitle() : "N/A"), gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        mainPanel.add(new JLabel("Điểm số:"), gbc);
        gbc.gridx = 1;
        JLabel scoreLabel = new JLabel(String.format("%.1f/10", selectedResult.getScore()));
        scoreLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        if (selectedResult.getScore() >= 8) {
            scoreLabel.setForeground(new Color(40, 167, 69)); // Green
        } else if (selectedResult.getScore() >= 6) {
            scoreLabel.setForeground(new Color(255, 193, 7)); // Yellow
        } else {
            scoreLabel.setForeground(new Color(220, 53, 69)); // Red
        }
        mainPanel.add(scoreLabel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        mainPanel.add(new JLabel("Thời gian bắt đầu:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(new JLabel(selectedResult.getStartTime().toString()), gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        mainPanel.add(new JLabel("Thời gian kết thúc:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(new JLabel(selectedResult.getEndTime().toString()), gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        mainPanel.add(new JLabel("Ngày nộp:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(new JLabel(selectedResult.getSubmittedAt().toString()), gbc);
        
        // Thời gian làm bài
        long durationMinutes = java.time.Duration.between(selectedResult.getStartTime(), selectedResult.getEndTime()).toMinutes();
        gbc.gridx = 0; gbc.gridy = 5;
        mainPanel.add(new JLabel("Thời gian làm bài:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(new JLabel(durationMinutes + " phút"), gbc);
        
        // Đánh giá
        gbc.gridx = 0; gbc.gridy = 6;
        mainPanel.add(new JLabel("Đánh giá:"), gbc);
        gbc.gridx = 1;
        String evaluation = getEvaluation(selectedResult.getScore());
        JLabel evaluationLabel = new JLabel(evaluation);
        evaluationLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        if (selectedResult.getScore() >= 8) {
            evaluationLabel.setForeground(new Color(40, 167, 69)); // Green
        } else if (selectedResult.getScore() >= 6) {
            evaluationLabel.setForeground(new Color(255, 193, 7)); // Yellow
        } else {
            evaluationLabel.setForeground(new Color(220, 53, 69)); // Red
        }
        mainPanel.add(evaluationLabel, gbc);
        
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

    private String getEvaluation(double score) {
        if (score >= 9) {
            return "Xuất sắc";
        } else if (score >= 8) {
            return "Giỏi";
        } else if (score >= 7) {
            return "Khá";
        } else if (score >= 6) {
            return "Trung bình";
        } else if (score >= 5) {
            return "Yếu";
        } else {
            return "Kém";
        }
    }
}
