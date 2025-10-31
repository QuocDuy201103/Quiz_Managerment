package com.quiz.ui.panels;

import com.quiz.dao.ExamDAO;
import com.quiz.model.Exam;
import com.quiz.model.User;
import com.quiz.ui.ExamTakingFrame;
import com.quiz.ui.QuizModeSelector;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel danh sách đề thi cho học sinh
 */
public class StudentExamPanel extends JPanel {
    private JTable examTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton startExamButton, refreshButton;
    private JScrollPane scrollPane;
    private ExamDAO examDAO;
    private List<Exam> exams;
    private User currentUser;

    public StudentExamPanel() {
        this(null);
    }

    public StudentExamPanel(User currentUser) {
        this.currentUser = currentUser;
        examDAO = new ExamDAO();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadExams();
    }

    private void initializeComponents() {
        // Table
        String[] columnNames = {"ID", "Tiêu đề", "Môn học", "Thời gian (phút)", "Số câu hỏi", "Ngày tạo"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        examTable = new JTable(tableModel);
        examTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        examTable.setRowHeight(30);
        examTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Set column widths for better data display
        examTable.getColumnModel().getColumn(0).setPreferredWidth(60);   // ID
        examTable.getColumnModel().getColumn(1).setPreferredWidth(250);  // Tiêu đề
        examTable.getColumnModel().getColumn(2).setPreferredWidth(150);  // Môn học
        examTable.getColumnModel().getColumn(3).setPreferredWidth(100);  // Thời gian
        examTable.getColumnModel().getColumn(4).setPreferredWidth(100);  // Số câu hỏi
        examTable.getColumnModel().getColumn(5).setPreferredWidth(150);  // Ngày tạo
        
        // Improve table header
        examTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        examTable.getTableHeader().setPreferredSize(new Dimension(0, 35));
        
        // Scroll pane with better sizing
        scrollPane = new JScrollPane(examTable);
        scrollPane.setPreferredSize(new Dimension(900, 450));
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        
        // Enable grid lines for better readability
        examTable.setShowGrid(true);
        examTable.setGridColor(new Color(220, 220, 220));
        
        // Improve selection colors
        examTable.setSelectionBackground(new Color(70, 130, 180));
        examTable.setSelectionForeground(Color.WHITE);
        
        // Search field
        searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        // Buttons
        startExamButton = new JButton("Bắt đầu thi", com.quiz.ui.IconUtil.load("/images/exam.png", 16, 16));
        refreshButton = new JButton("Làm mới", com.quiz.ui.IconUtil.load("/images/refresh.png", 16, 16));
        
        Font buttonFont = new Font("Segoe UI", Font.PLAIN, 12);
        startExamButton.setFont(buttonFont);
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
        topPanel.add(startExamButton);
        topPanel.add(refreshButton);
        
        // Center panel - Table
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }

    private void setupEventHandlers() {
        startExamButton.addActionListener(e -> startSelectedExam());
        refreshButton.addActionListener(e -> loadExams());
        
        // Double click to start exam
        examTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    startSelectedExam();
                }
            }
        });
        
        // Live search when typing
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { filterExams(); }

            @Override
            public void removeUpdate(DocumentEvent e) { filterExams(); }

            @Override
            public void changedUpdate(DocumentEvent e) { filterExams(); }
        });
    }

    private void loadExams() {
        exams = examDAO.getAllExams();
        updateTable();
    }

    private void updateTable() {
        updateTable(exams);
    }

    private void updateTable(List<Exam> toShow) {
        tableModel.setRowCount(0);
        for (Exam exam : toShow) {
            Object[] row = {
                exam.getId(),
                exam.getTitle(),
                exam.getSubject() != null ? exam.getSubject().getName() : "N/A",
                exam.getDuration(),
                exam.getQuestionCount(),
                exam.getCreatedAt().toString().substring(0, 19)
            };
            tableModel.addRow(row);
        }
    }

    private void filterExams() {
        String searchText = normalizeString(searchField.getText());
        if (searchText.isEmpty()) { updateTable(exams); return; }
        List<Exam> filtered = new ArrayList<>();
        for (Exam exam : exams) {
            String haystack = buildSearchableText(exam);
            boolean matches = true;
            for (String token : searchText.split("\\s+")) {
                if (!haystack.contains(token)) { matches = false; break; }
            }
            if (matches) filtered.add(exam);
        }
        updateTable(filtered);
    }

    private String buildSearchableText(Exam exam) {
        StringBuilder sb = new StringBuilder();
        if (exam.getTitle() != null) sb.append(exam.getTitle()).append(' ');
        if (exam.getSubject() != null && exam.getSubject().getName() != null) sb.append(exam.getSubject().getName());
        return normalizeString(sb.toString());
    }

    private String normalizeString(String input) {
        if (input == null) return "";
        String lowered = input.toLowerCase().trim().replaceAll("\\s+", " ");
        String decomposed = Normalizer.normalize(lowered, Normalizer.Form.NFD);
        return decomposed.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    private void startSelectedExam() {
        int selectedRow = examTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đề thi để bắt đầu!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Exam selectedExam = exams.get(selectedRow);
        Exam examWithQuestions = examDAO.getExamById(selectedExam.getId());
        
        if (examWithQuestions == null || examWithQuestions.getQuestions() == null || examWithQuestions.getQuestions().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Đề thi này chưa có câu hỏi!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int option = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc chắn muốn bắt đầu thi đề '" + selectedExam.getTitle() + "'?\n" +
            "Thời gian: " + selectedExam.getDuration() + " phút\n" +
            "Số câu hỏi: " + examWithQuestions.getQuestions().size(), 
            "Xác nhận bắt đầu thi", JOptionPane.YES_NO_OPTION);
        
        if (option == JOptionPane.YES_OPTION) {
            // Show quiz mode selector
            QuizModeSelector.QuizMode selectedMode = QuizModeSelector.showModeSelector(
                (JFrame) SwingUtilities.getWindowAncestor(this), 
                examWithQuestions, 
                currentUser
            );
            
            if (selectedMode != null) {
                // Create exam frame with selected mode
                ExamTakingFrame.QuizMode mode = convertToExamMode(selectedMode);
                ExamTakingFrame examFrame = new ExamTakingFrame(examWithQuestions, currentUser, mode);
                examFrame.setVisible(true);
            }
        }
    }
    
    private ExamTakingFrame.QuizMode convertToExamMode(QuizModeSelector.QuizMode mode) {
        switch (mode) {
            case EASY:
                return ExamTakingFrame.QuizMode.EASY;
            case MEDIUM:
                return ExamTakingFrame.QuizMode.MEDIUM;
            case HARD:
                return ExamTakingFrame.QuizMode.HARD;
            case QUIZ_GAME:
                return ExamTakingFrame.QuizMode.QUIZ_GAME;
            default:
                return ExamTakingFrame.QuizMode.EASY;
        }
    }
}
