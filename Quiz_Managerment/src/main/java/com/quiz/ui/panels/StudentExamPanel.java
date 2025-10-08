package com.quiz.ui.panels;

import com.quiz.dao.ExamDAO;
import com.quiz.model.Exam;
import com.quiz.model.User;
import com.quiz.ui.ExamTakingFrame;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel danh sách đề thi cho học sinh
 */
public class StudentExamPanel extends JPanel {
    private JTable examTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton startExamButton, refreshButton;
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
        examTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        examTable.setRowHeight(25);
        examTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
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
        centerPanel.add(new JScrollPane(examTable), BorderLayout.CENTER);
        
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
    }

    private void loadExams() {
        exams = examDAO.getAllExams();
        updateTable();
    }

    private void updateTable() {
        tableModel.setRowCount(0);
        for (Exam exam : exams) {
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
            // Mở giao diện thi
            ExamTakingFrame examFrame = new ExamTakingFrame(examWithQuestions, currentUser);
            examFrame.setVisible(true);
        }
    }
}
