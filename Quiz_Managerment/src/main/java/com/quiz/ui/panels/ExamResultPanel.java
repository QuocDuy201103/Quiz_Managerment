package com.quiz.ui.panels;

import com.quiz.bus.ExamResultService;
import com.quiz.model.ExamResult;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel xem kết quả thi (cho admin và teacher)
 */
public class ExamResultPanel extends JPanel {
    private JTable resultTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton refreshButton, viewDetailsButton;
    private JScrollPane scrollPane;
    private ExamResultService examResultService;
    private List<ExamResult> examResults;

    public ExamResultPanel() {
        examResultService = new ExamResultService();
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
        resultTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        resultTable.setRowHeight(30);
        resultTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Set column widths for better data display
        resultTable.getColumnModel().getColumn(0).setPreferredWidth(60);   // ID
        resultTable.getColumnModel().getColumn(1).setPreferredWidth(120);  // Học sinh
        resultTable.getColumnModel().getColumn(2).setPreferredWidth(200);  // Đề thi
        resultTable.getColumnModel().getColumn(3).setPreferredWidth(80);   // Điểm
        resultTable.getColumnModel().getColumn(4).setPreferredWidth(150);  // Thời gian bắt đầu
        resultTable.getColumnModel().getColumn(5).setPreferredWidth(150);  // Thời gian kết thúc
        resultTable.getColumnModel().getColumn(6).setPreferredWidth(150);  // Ngày nộp
        
        // Improve table header
        resultTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        resultTable.getTableHeader().setPreferredSize(new Dimension(0, 35));
        
        // Scroll pane with better sizing
        scrollPane = new JScrollPane(resultTable);
        scrollPane.setPreferredSize(new Dimension(1000, 450));
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        
        // Enable grid lines for better readability
        resultTable.setShowGrid(true);
        resultTable.setGridColor(new Color(220, 220, 220));
        
        // Improve selection colors
        resultTable.setSelectionBackground(new Color(70, 130, 180));
        resultTable.setSelectionForeground(Color.WHITE);
        
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
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
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

        // Live search when typing
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { filterResults(); }

            @Override
            public void removeUpdate(DocumentEvent e) { filterResults(); }

            @Override
            public void changedUpdate(DocumentEvent e) { filterResults(); }
        });
    }

    private void loadExamResults() {
        List<ExamResult> results = examResultService.getAllResults();
        tableModel.setRowCount(0);
        for (ExamResult r : results) {
            Object[] row = {
                r.getId(),
                r.getUser() != null ? r.getUser().getUsername() : r.getUserId(),
                r.getExam() != null ? r.getExam().getTitle() : r.getExamId(),
                r.getScore(),
                r.getStartTime() != null ? r.getStartTime().toString().substring(0, 19) : "",
                r.getEndTime() != null ? r.getEndTime().toString().substring(0, 19) : "",
                r.getSubmittedAt() != null ? r.getSubmittedAt().toString().substring(0, 19) : ""
            };
            tableModel.addRow(row);
        }
    }

    private void updateTable() {
        updateTable(examResults);
    }

    private void updateTable(List<ExamResult> toShow) {
        tableModel.setRowCount(0);
        for (ExamResult result : toShow) {
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

    private void filterResults() {
        String searchText = normalizeString(searchField.getText());
        if (searchText.isEmpty()) { updateTable(examResults); return; }
        List<ExamResult> filtered = new ArrayList<>();
        for (ExamResult result : examResults) {
            String haystack = buildSearchableText(result);
            boolean matches = true;
            for (String token : searchText.split("\\s+")) {
                if (!haystack.contains(token)) { matches = false; break; }
            }
            if (matches) filtered.add(result);
        }
        updateTable(filtered);
    }

    private String buildSearchableText(ExamResult result) {
        StringBuilder sb = new StringBuilder();
        if (result.getUser() != null && result.getUser().getUsername() != null) sb.append(result.getUser().getUsername()).append(' ');
        if (result.getExam() != null && result.getExam().getTitle() != null) sb.append(result.getExam().getTitle()).append(' ');
        sb.append(String.format("%.1f", result.getScore())).append(' ');
        if (result.getStartTime() != null) sb.append(result.getStartTime().toString()).append(' ');
        if (result.getEndTime() != null) sb.append(result.getEndTime().toString()).append(' ');
        if (result.getSubmittedAt() != null) sb.append(result.getSubmittedAt().toString());
        return normalizeString(sb.toString());
    }

    private String normalizeString(String input) {
        if (input == null) return "";
        String lowered = input.toLowerCase().trim().replaceAll("\\s+", " ");
        String decomposed = Normalizer.normalize(lowered, Normalizer.Form.NFD);
        return decomposed.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
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
